package com.xjh.gestureheart.mediacodec;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 25623 on 2018/5/8.
 */

public class AudioMixer {
    private final String TAG = "AudioMixer";
    private final String AUDIO_MIME_TYPE_PREFIX = "audio/";
    private final int AUDIO_SAMPLE_RATE = 44100;
    private final int AUDIO_CHANNEL_COUNT = 2;

    final int TIMEOUT_USEC = 100;


    private String mOriginalMediaInputPath;
    private String mNewMediaInputPath;
    private String mAudioOutputPath;
    private float mOriginalMediaVolume;
    private float mNewMeidaVolume;
    private MediaCodec mOriginalAudioDecoder;
    private MediaCodec mNewAudioDecoder;
    private MediaCodec audioEncoder;

    private long mNewMediaStartTimestamp;
    private MediaFormat mOriginalAudioInputFormat;
    private MediaFormat mNewAudioInputFormat;
    private AudioTrack audioTrack;
    private MediaExtractor mOriginalAudioExtractor;
    private MediaExtractor mNewAudioExtractor;

    private ReentrantLock mLock = new ReentrantLock();
    private Condition mOriginalDecodeCondition = mLock.newCondition();
    private Condition mNewDecodeCondition = mLock.newCondition();
    private Condition mOriginalEncodeCondition = mLock.newCondition();
    private Condition mNewEncodeCondition = mLock.newCondition();

    private final int PCM_MAX_COUNT = 16;
    private final int PCM_DATA_LENGTH = 4096;
    private byte[][] mOriginalPCM = new byte[PCM_MAX_COUNT][PCM_DATA_LENGTH];
    private byte[] mOriginalPCMByte = new byte[PCM_DATA_LENGTH];
    private short[] mOriginalPCMShort = new short[PCM_DATA_LENGTH / 2];
    private int mOriginalPCMCount = 0;
    private int mOriginalPCMReadIndex = 0;
    private int mOriginalPCMWriteIndex = 0;
    private long[] mOriginalTimestamp = new long[PCM_MAX_COUNT];
    private int mOriginalTimestampCount = 0;
    private int mOriginalTimestampReadIndex = 0;
    private int mOriginalTimestampWriteIndex = 0;

    private byte[][] mNewPCM = new byte[PCM_MAX_COUNT][PCM_DATA_LENGTH];
    private byte[] mNewPCMByte = new byte[PCM_DATA_LENGTH];
    private short[] mNewPCMShort = new short[PCM_DATA_LENGTH / 2];
    private int mNewPCMCount = 0;
    private int mNewPCMReadIndex = 0;
    private int mNewPCMWriteIndex = 0;
    private boolean mOriginalAudioDecoderDone;
    private boolean mNewAudioDecoderDone;


    private byte[][] mMixedPCM = new byte[PCM_MAX_COUNT][PCM_DATA_LENGTH];
    private int mMixedPCMCount = 0;
    private int mMixedPCMReadIndex = 0;
    private int mMixedPCMWriteIndex = 0;

    private boolean originalDecodeDone;

    private BufferedOutputStream bos;


    public AudioMixer() {
        prepareAudioTrack();
    }

    private void prepareAudioTrack() {
        //启动AudioTrack，这是个播放器，可以播放PCM格式的数据。如果有需要可以用到。不需要播放的直接删掉就可以了。
        int buffsize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        //创建AudioTrack对象
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                48000,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STREAM);
        audioTrack.play();
    }

    /**
     * 给originalMediaInputPath加入新的音乐newMediaInputPath， 长度以originalMediaInputPath为准
     *
     * @param originalMediaInputPath
     * @param newMediaInputPath
     */
    public void setInputPath(String originalMediaInputPath, String newMediaInputPath) {
        mOriginalMediaInputPath = originalMediaInputPath;
        mNewMediaInputPath = newMediaInputPath;

        prepareExtractor();
        prepareCodec();
    }


    /**
     * @param originalMediaVolume 原音频的音量
     * @param newMeidaVolume      新音频的音量
     */
    public void setVolume(float originalMediaVolume, float newMeidaVolume) {
        mOriginalMediaVolume = originalMediaVolume;
        mNewMeidaVolume = newMeidaVolume;
    }


    /**
     * 新视频的开始timestamp
     *
     * @param startTimestamp
     */
    public void setNewMediaStartTimestamp(long startTimestamp) {
        mNewMediaStartTimestamp = startTimestamp;
    }

    public void setOutputPath(String audioOutputPath) {
        mAudioOutputPath = audioOutputPath;

        try {
            FileOutputStream fos = new FileOutputStream(new File(audioOutputPath));
            bos = new BufferedOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        MediaThreadPool.executorService.execute(new OriginalAudioDecodeRunnable());
        MediaThreadPool.executorService.execute(new NewAudioDecodeRunnable());
        MediaThreadPool.executorService.execute(new AudioMixRunnable());
//        MediaThreadPool.executorService.execute(new AudioMuxerRunnable());
    }

    private void prepareExtractor() {
        mOriginalAudioExtractor = createExtractor(mOriginalMediaInputPath);
        int audioInputTrackIndex = getTrackIndex(mOriginalAudioExtractor, AUDIO_MIME_TYPE_PREFIX);
        mOriginalAudioExtractor.selectTrack(audioInputTrackIndex);
        mOriginalAudioInputFormat = mOriginalAudioExtractor.getTrackFormat(audioInputTrackIndex);


        mNewAudioExtractor = createExtractor(mNewMediaInputPath);
        audioInputTrackIndex = getTrackIndex(mNewAudioExtractor, AUDIO_MIME_TYPE_PREFIX);
        mNewAudioExtractor.selectTrack(audioInputTrackIndex);
        mNewAudioInputFormat = mNewAudioExtractor.getTrackFormat(audioInputTrackIndex);
    }

    private MediaExtractor createExtractor(String dataSource) {
        MediaExtractor extractor = null;
        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(dataSource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractor;
    }

    private int getTrackIndex(MediaExtractor extractor, String mimeTypePrefix) {
        int trackCount = extractor.getTrackCount();
        for (int index = 0; index < trackCount; ++index) {
            if (getMimeType(extractor.getTrackFormat(index)).startsWith(mimeTypePrefix)) {
                return index;
            }
        }
        return -1;
    }

    private String getMimeType(MediaFormat format) {
        return format.getString(MediaFormat.KEY_MIME);
    }


    private void prepareCodec() {
        try {
            mOriginalAudioDecoder = MediaCodec.createDecoderByType("audio/mp4a-latm");
            mNewAudioDecoder = MediaCodec.createDecoderByType("audio/mp4a-latm");
            audioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class OriginalAudioDecodeRunnable implements Runnable {
        @Override
        public void run() {
            initAudioDecode(mOriginalAudioDecoder, mOriginalAudioInputFormat);
            decodeOriginalAudio(mOriginalAudioDecoder, mOriginalAudioExtractor);
        }
    }


    private class NewAudioDecodeRunnable implements Runnable {
        @Override
        public void run() {
            initAudioDecode(mNewAudioDecoder, mNewAudioInputFormat);
            decodeNewAudio(mNewAudioDecoder, mNewAudioExtractor);
        }
    }

    private class AudioMixRunnable implements Runnable {

        @Override
        public void run() {
            MediaFormat format = MediaFormat.createAudioFormat("audio/mp4a-latm", AUDIO_SAMPLE_RATE, /*channelCount*/AUDIO_CHANNEL_COUNT);//这里一定要注意声道的问题
            format.setInteger(MediaFormat.KEY_BIT_RATE, 128000);//比特率
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            audioEncoder.start();

            encodeAudio(audioEncoder);

            release();
        }
    }

    private void release() {

        Log.e(TAG, " release 开始");
        if (mOriginalAudioDecoder != null) {
            mOriginalAudioDecoder.stop();
            mOriginalAudioDecoder.release();
            mOriginalAudioDecoder = null;
        }

        if (mNewAudioDecoder != null) {
            mNewAudioDecoder.stop();
            mNewAudioDecoder.release();
            mNewAudioDecoder = null;
        }


        Log.e(TAG, " release 结束");

    }

    private void initAudioDecode(MediaCodec audioDecoder, MediaFormat mediaFormat) {
        audioDecoder.configure(mediaFormat, null, null, 0);
        audioDecoder.start();
    }


    private void decodeOriginalAudio(MediaCodec audioDecoder, MediaExtractor audioExtractor) {
        ByteBuffer[] decoderInputBuffers = audioDecoder.getInputBuffers();
        ByteBuffer[] decoderOutputBuffers = audioDecoder.getOutputBuffers();

        MediaCodec.BufferInfo decoderBufferInfo = new MediaCodec.BufferInfo();
        boolean done = false;//用于判断整个编解码过程是否结束
        boolean inputDone = false;
        originalDecodeDone = false;
        long presentationTimeUS;

        int decodeOriginalCount = 0;

        while (!originalDecodeDone) {
            if (!inputDone) {
                int inputIndex = audioDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputIndex >= 0) {
                    presentationTimeUS = audioExtractor.getSampleTime();

                    ByteBuffer inputBuffer = decoderInputBuffers[inputIndex];
                    inputBuffer.clear();
                    int size = audioExtractor.readSampleData(inputBuffer, 0);
                    if (size > 0) {
                        audioDecoder.queueInputBuffer(inputIndex, 0, size, presentationTimeUS, audioExtractor.getSampleFlags());
                    } else {
                        audioDecoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        System.out.println("videoCliper audio decodeInput end");
                        inputDone = true;
                    }

                    audioExtractor.advance();
                }
            }
            if (!originalDecodeDone) {
                int index = audioDecoder.dequeueOutputBuffer(decoderBufferInfo, TIMEOUT_USEC);
                if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    decoderOutputBuffers = audioDecoder.getOutputBuffers();
                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // expected before first buffer of data
                    MediaFormat decoderOutputAudioFormat = audioDecoder.getOutputFormat();
                    Log.e(TAG, "playbackRate = " + decoderOutputAudioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                    audioTrack.setPlaybackRate(decoderOutputAudioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                } else {
                    if (decoderBufferInfo.size > 0) {
                        try {
                            mLock.lock();
                            if (isPCMFull(mOriginalPCMCount)) {
                                Log.i(TAG, "CONDITION original decode await 开始");
                                mOriginalDecodeCondition.await();
                                Log.i(TAG, "CONDITION original decode await 结束");
                            }

                            Log.i(TAG, " audioCount decodeOriginalCount = " + (++decodeOriginalCount));
                            ByteBuffer decoderOutputBuffer = decoderOutputBuffers[index];
                            writeOriginalPCM(decoderOutputBuffer, decoderBufferInfo.offset, decoderBufferInfo.size);
                            writeOriginalTimestamp(decoderBufferInfo.presentationTimeUs);

                            Log.i(TAG, "CONDITION original encode signal 开始");
                            mOriginalEncodeCondition.signal();
                            Log.i(TAG, "CONDITION original encode signal 结束");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            mLock.unlock();
                        }

                    } else {
                        System.out.println("videoCliper audio encodeInput end");
                        originalDecodeDone = true;
                    }
                    audioDecoder.releaseOutputBuffer(index, false);
                }
            }
        }
    }

    private void decodeNewAudio(MediaCodec audioDecoder, MediaExtractor audioExtractor) {
        ByteBuffer[] decoderInputBuffers = audioDecoder.getInputBuffers();
        ByteBuffer[] decoderOutputBuffers = audioDecoder.getOutputBuffers();

        MediaCodec.BufferInfo decoderBufferInfo = new MediaCodec.BufferInfo();
        boolean done = false;//用于判断整个编解码过程是否结束
        boolean inputDone = false;
        boolean decodeDone = false;

        while (!done) {
            if (!inputDone) {
                int inputIndex = audioDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputIndex >= 0) {
                    long presentationTimeUS = audioExtractor.getSampleTime();

                    ByteBuffer inputBuffer = decoderInputBuffers[inputIndex];
                    inputBuffer.clear();
                    int size = audioExtractor.readSampleData(inputBuffer, 0);
                    if (size > 0) {
                        audioDecoder.queueInputBuffer(inputIndex, 0, size, presentationTimeUS, audioExtractor.getSampleFlags());
                    } else {
                        audioDecoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        System.out.println("videoCliper audio decodeInput end");
                        inputDone = true;
                    }

                    audioExtractor.advance();
                }
            }
            if (!decodeDone) {
                int index = audioDecoder.dequeueOutputBuffer(decoderBufferInfo, TIMEOUT_USEC);
                if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    decoderOutputBuffers = audioDecoder.getOutputBuffers();
                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // expected before first buffer of data
                    MediaFormat decoderOutputAudioFormat = audioDecoder.getOutputFormat();
                    Log.e(TAG, "playbackRate = " + decoderOutputAudioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                    audioTrack.setPlaybackRate(decoderOutputAudioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                } else {
                    if (decoderBufferInfo.size > 0) {

                        try {
                            mLock.lock();
                            if (isPCMFull(mNewPCMCount)) {
                                Log.e(TAG, "CONDITION new decode await 开始 mNewPCMCount = " + mNewPCMCount);
                                mNewDecodeCondition.await();
                                Log.e(TAG, "CONDITION new decode await 结束");
                            }
                            ByteBuffer decoderOutputBuffer = decoderOutputBuffers[index];
                            writeNewPCM(decoderOutputBuffer, decoderBufferInfo.offset, decoderBufferInfo.size);

                            Log.e(TAG, "CONDITION new encode signal 开始");
                            mNewEncodeCondition.signal();
                            Log.e(TAG, "CONDITION new encode signal 结束");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            mLock.unlock();
                        }

                    } else {
                        System.out.println("videoCliper audio encodeInput end");
                        decodeDone = true;
                    }
                    audioDecoder.releaseOutputBuffer(index, false);
                }
            }
        }
    }


    private void encodeAudio(MediaCodec audioEncoder) {
        ByteBuffer[] encoderInputBuffers = audioEncoder.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = audioEncoder.getOutputBuffers();
        MediaCodec.BufferInfo encoderBufferInfo = new MediaCodec.BufferInfo();

        int encodeInputIndex;
        boolean muxDone = false;
        int encodeAudioCount = 0;


        while (!muxDone) {
            byte[] mixedPCM = mixPCM();
            audioTrack.write(mixedPCM, 0, mixedPCM.length);
            Log.e(TAG, " audioCount   encodeAudioCount = " + (++encodeAudioCount));


            encodeInputIndex = audioEncoder.dequeueInputBuffer(TIMEOUT_USEC);
            Log.e(TAG, "encodeAudio  encodeInputIndex = " + encodeInputIndex);
            if (encodeInputIndex > 0) {
                ByteBuffer encoderInputBuffer = encoderInputBuffers[encodeInputIndex];
                encoderInputBuffer.put(mixedPCM);
                audioEncoder.queueInputBuffer(encodeInputIndex, 0, mixedPCM.length, readOriginalTimestamp(), 0);

                int encodeOutputIndex = audioEncoder.dequeueOutputBuffer(encoderBufferInfo, TIMEOUT_USEC);

                try{
                    if (encodeOutputIndex > 0) {
                        //从编码器中取出数据
                        int outBitSize = encoderBufferInfo.size;
                        int outPacketSize = outBitSize + 7;//7为ADTS头部的大小
                        ByteBuffer outputBuffer = encoderOutputBuffers[encodeOutputIndex];//拿到输出Buffer
                        outputBuffer.position(encoderBufferInfo.offset);
                        outputBuffer.limit(encoderBufferInfo.offset + outBitSize);
                        byte[] chunkAudio = new byte[outPacketSize];
                        AudioCodec.addADTStoPacket(chunkAudio, outPacketSize);//添加ADTS 代码后面会贴上
                        outputBuffer.get(chunkAudio, 7, outBitSize);//将编码得到的AAC数据 取出到byte[]中 偏移量offset=7 你懂得
                        outputBuffer.position(encoderBufferInfo.offset);
                        Log.e("hero", "--编码成功-写入文件----" + chunkAudio.length);
                        bos.write(chunkAudio, 0, chunkAudio.length);//BufferOutputStream 将文件保存到内存卡中 *.aac
                        bos.flush();

                        audioEncoder.releaseOutputBuffer(encodeOutputIndex, false);

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


                Log.i(TAG, "encodeAudio encodeInputIndex = " + encodeInputIndex + "  encoderStatus = " + encodeOutputIndex);
            }
        }
    }

    private byte[] mixPCM() {
        mLock.lock();
        try {

            byte[] mixedPCMByte = new byte[PCM_DATA_LENGTH];
            short[] mMixedPCMShort = new short[PCM_DATA_LENGTH / 2];


            if (isPCMEmpty(mOriginalPCMCount)) {
                Log.i(TAG, "CONDITION original decode signal 开始 ======");
                mOriginalDecodeCondition.signal();
                Log.i(TAG, "CONDITION original decode signal 结束 ======");

                Log.i(TAG, "CONDITION original encode await 开始 ======");
                mOriginalEncodeCondition.await();
                Log.i(TAG, "CONDITION original encode await 结束 ======");

            }

            if (isPCMEmpty(mNewPCMCount)) {
                Log.e(TAG, "CONDITION new decode signal 开始 ======");
                mNewDecodeCondition.signal();
                Log.e(TAG, "CONDITION new decode signal 结束 ======");

                Log.e(TAG, "CONDITION new encode await 开始 ======");
                mNewEncodeCondition.await();
                Log.e(TAG, "CONDITION new encode await 结束 ======");
            }

            //转化为short类型，便于运算
            mOriginalPCMByte = readOriginalPCM();
            for (int i = 0; i < PCM_DATA_LENGTH / 2; ++i) {
                mOriginalPCMShort[i] = (short) (((short) ((mOriginalPCMByte[i * 2] & 0xff) | (mOriginalPCMByte[i * 2 + 1] & 0xff) << 8)) * mOriginalMediaVolume);
            }
            mNewPCMByte = readNewPCM();
            for (int i = 0; i < PCM_DATA_LENGTH / 2; ++i) {
                mNewPCMShort[i] = (short) ((short) ((mNewPCMByte[i * 2] & 0xff) | (mNewPCMByte[i * 2 + 1] & 0xff) << 8) * mNewMeidaVolume);
            }

            //混合short
            for (int i = 0; i < PCM_DATA_LENGTH / 2; ++i) {
                mMixedPCMShort[i] = (short) ((mOriginalPCMShort[i] + mNewPCMShort[i]) / 2);
            }

            //转换为byte
            for (int i = 0; i < PCM_DATA_LENGTH / 2; ++i) {
                mixedPCMByte[i * 2] = (byte) (mMixedPCMShort[i] & 0x00FF);
                mixedPCMByte[i * 2 + 1] = (byte) ((mMixedPCMShort[i] & 0xFF00) >> 8);
            }

            return mixedPCMByte;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }

        return null;
    }

    private void writeOriginalPCM(ByteBuffer byteBuffer, int bufferOffset, int bufferSize) {
        if (bufferSize < 4096) {
            byte[] chunkPCM = new byte[bufferSize];
            byteBuffer.get(chunkPCM);
            byteBuffer.clear();
            //说明是单声道的,需要转换一下
            for (int i = 0; i < bufferSize; i += 2) {
                mOriginalPCM[mOriginalPCMWriteIndex][i * 2 + 0] = chunkPCM[i];
                mOriginalPCM[mOriginalPCMWriteIndex][i * 2 + 1] = chunkPCM[i + 1];
                mOriginalPCM[mOriginalPCMWriteIndex][i * 2 + 2] = chunkPCM[i];
                mOriginalPCM[mOriginalPCMWriteIndex][i * 2 + 3] = chunkPCM[i + 1];
            }
        } else {
            /**播放音频数据**/
            //将Buffer内的数据取出到字节数组中
            byteBuffer.get(mOriginalPCM[mOriginalPCMWriteIndex]);
        }

        ++mOriginalPCMWriteIndex;
        if (mOriginalPCMWriteIndex >= PCM_MAX_COUNT) {
            mOriginalPCMWriteIndex = 0;
        }
        ++mOriginalPCMCount;


    }

    private byte[] readOriginalPCM() {
        byte[] pcm = mOriginalPCM[mOriginalPCMReadIndex];

        ++mOriginalPCMReadIndex;
        if (mOriginalPCMReadIndex >= PCM_MAX_COUNT) {
            mOriginalPCMReadIndex = 0;
        }
        --mOriginalPCMCount;

        return pcm;
    }

    private void writeNewPCM(ByteBuffer byteBuffer, int bufferOffset, int bufferSize) {
        if (bufferSize < 4096) {
            byte[] chunkPCM = new byte[bufferSize];
            byteBuffer.get(chunkPCM);
            byteBuffer.clear();
            //说明是单声道的,需要转换一下
            for (int i = 0; i < bufferSize; i += 2) {
                mNewPCM[mNewPCMWriteIndex][i * 2 + 0] = chunkPCM[i];
                mNewPCM[mNewPCMWriteIndex][i * 2 + 1] = chunkPCM[i + 1];
                mNewPCM[mNewPCMWriteIndex][i * 2 + 2] = chunkPCM[i];
                mNewPCM[mNewPCMWriteIndex][i * 2 + 3] = chunkPCM[i + 1];
            }
        } else {
            /**播放音频数据**/
            //将Buffer内的数据取出到字节数组中
            byteBuffer.get(mNewPCM[mNewPCMWriteIndex]);
        }


        ++mNewPCMWriteIndex;
        if (mNewPCMWriteIndex >= PCM_MAX_COUNT) {
            mNewPCMWriteIndex = 0;
        }
        ++mNewPCMCount;


    }

    private byte[] readNewPCM() {
        byte[] pcm = mNewPCM[mNewPCMReadIndex];

        ++mNewPCMReadIndex;
        if (mNewPCMReadIndex >= PCM_MAX_COUNT) {
            mNewPCMReadIndex = 0;
        }
        --mNewPCMCount;

        return pcm;
    }

    private boolean isPCMFull(int PCMCount) {
        if (PCMCount == PCM_MAX_COUNT) {
            return true;
        }
        return false;
    }

    private boolean isPCMEmpty(int PCMCount) {
        if (PCMCount == 0) {
            return true;
        }
        return false;
    }


    private void writeOriginalTimestamp(long timestamp) {
        mOriginalTimestamp[mOriginalTimestampWriteIndex] = timestamp;
        ++mOriginalTimestampWriteIndex;
        if (mOriginalTimestampWriteIndex >= PCM_MAX_COUNT) {
            mOriginalTimestampWriteIndex = 0;
        }
    }

    private long readOriginalTimestamp() {
        long timestamp = mOriginalTimestamp[mOriginalTimestampReadIndex];
        ++mOriginalTimestampReadIndex;
        if (mOriginalTimestampReadIndex >= PCM_MAX_COUNT) {
            mOriginalTimestampReadIndex = 0;
        }
        return timestamp;
    }

}
