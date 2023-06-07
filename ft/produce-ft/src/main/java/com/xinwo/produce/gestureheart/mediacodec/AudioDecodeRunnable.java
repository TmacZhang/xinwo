package com.xinwo.produce.gestureheart.mediacodec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.util.SparseLongArray;

import java.nio.ByteBuffer;

/**
 * Created by 25623 on 2018/2/4.
 */

public class AudioDecodeRunnable implements Runnable {
    private final String TAG = "AudioDecodeRunnable";
    private static final int TIMEOUT_USEC = 10000;

    private final CavalryMediaPlayer mMediaPlayer;

    private boolean mDecodingQueueInputBuffer = false;

    private SingleLockObject mDecodeLock = new SingleLockObject();
    private boolean audioExtractorDone;
    private boolean audioDecoderDone;

    private SparseLongArray mShakeTimestampArray = new SparseLongArray();
    private int mShakePointsCount = 0;


    public AudioDecodeRunnable(CavalryMediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    public void startDecode() {
        mDecodingQueueInputBuffer = true;

        audioExtractorDone = false;
        audioDecoderDone = false;

        mDecodeLock.signal();
    }

    public void pauseDecode() {
        mDecodingQueueInputBuffer = false;
    }

    @Override
    public void run() {
        doExtractDecodeEditEncodeMux();
    }


    /**
     * Does the actual work for extracting, decoding, encoding and muxing.
     */
    private void doExtractDecodeEditEncodeMux() {

        ByteBuffer[] audioDecoderInputBuffers = mMediaPlayer.audioDecoder.getInputBuffers();
        ByteBuffer[] audioDecoderOutputBuffers = mMediaPlayer.audioDecoder.getOutputBuffers();

        MediaCodec.BufferInfo audioDecoderOutputBufferInfo = new MediaCodec.BufferInfo();

        int decoderInputBufferIndex = -1;
        int decoderOutputBufferIndex = -1;

        audioExtractorDone = false;
        audioDecoderDone = false;

        while (true) {

            if (!mDecodingQueueInputBuffer) {
                mDecodeLock.await();
                Log.e(TAG,"1934 --> await 结束 重新开始");
            }

            if(audioDecoderDone){
                mMediaPlayer.audioDecoder.flush();
                mDecodeLock.await();
            }

            //从文件读取数据
            while (!audioExtractorDone) {
                decoderInputBufferIndex = mMediaPlayer.audioDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (decoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    Log.d(TAG, "1.从文件读取数据 --> INFO_TRY_AGAIN_LATER:   no audio decoder input buffer");
                    break;
                }

                long presentationTimeUS = mMediaPlayer.audioExtractor.getSampleTime();

                Log.e(TAG,"SYNC sample Audio = " + presentationTimeUS);

                ByteBuffer decoderInputBuffer = audioDecoderInputBuffers[decoderInputBufferIndex];
                int size = mMediaPlayer.audioExtractor.readSampleData(decoderInputBuffer, 0);
                long presentationTime = mMediaPlayer.audioExtractor.getSampleTime();

                Log.d(TAG, "1.从文件读取数据 --> readSampleData size = " + size);

                if(size >= 0){
                    Log.d(TAG, "1.从文件读取数据 --> size >= 0");
                    mMediaPlayer.audioDecoder.queueInputBuffer(
                            decoderInputBufferIndex,
                            0,
                            size,
                            presentationTime,
                            mMediaPlayer.audioExtractor.getSampleFlags());



                }else{
                    Log.d(TAG, "1.从文件读取数据 --> audio extractor: EOS");
//                    mMediaPlayer.audioDecoder.queueInputBuffer(
//                            decoderInputBufferIndex,
//                            0,
//                            0,
//                            0,
//                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);

                    audioExtractorDone = true;
                }


                mMediaPlayer.audioExtractor.advance();
                break;
            }// end of while 从文件读取数据

            //解码读出的数据
            while(!audioDecoderDone){
                decoderOutputBufferIndex = mMediaPlayer.audioDecoder.dequeueOutputBuffer(audioDecoderOutputBufferInfo, TIMEOUT_USEC);
                switch (decoderOutputBufferIndex){
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        Log.e(TAG, "2.解码读出的数据 --> INFO_TRY_AGAIN_LATER:   no audio decoder output buffer");
                        if(audioExtractorDone){
                            audioDecoderDone = true;
                        }
                        break;
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        Log.e(TAG, "2.解码读出的数据 --> INFO_OUTPUT_BUFFERS_CHANGED");
                        audioDecoderOutputBuffers = mMediaPlayer.audioDecoder.getOutputBuffers();
                        break;
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        Log.e(TAG, "2.解码读出的数据 --> INFO_OUTPUT_FORMAT_CHANGED");
                        MediaFormat decoderOutputAudioFormat = mMediaPlayer.audioDecoder.getOutputFormat();
                        mMediaPlayer.audioTrack.setPlaybackRate(decoderOutputAudioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                        break;
                    default:
                        Log.e(TAG, "2.解码读出的数据 --> default");
                        if ((audioDecoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            Log.e(TAG, "解码读出的数据 --> audio decoder: codec config buffer");
                            mMediaPlayer.audioDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
                            break;
                        }
                        ByteBuffer decoderOutputBuffer =
                                audioDecoderOutputBuffers[decoderOutputBufferIndex]
                                        .duplicate();

                        Log.e(TAG, "播放音频数据 --> audioDecoderOutputBufferInfo.size = " + audioDecoderOutputBufferInfo.size);
                        if(audioDecoderOutputBufferInfo.size > 0){
                            /**播放音频数据**/
                            //BufferInfo内定义了此数据块的大小
                            final byte[] chunk = new byte[audioDecoderOutputBufferInfo.size];
                            //  createFileWithByte(chunk);
                            //将Buffer内的数据取出到字节数组中
                            decoderOutputBuffer.get(chunk);
                            mMediaPlayer.audioTrack.write(chunk,
                                    audioDecoderOutputBufferInfo.offset,
                                    audioDecoderOutputBufferInfo.offset + audioDecoderOutputBufferInfo.size);

                            Log.e(TAG,"audioDecoderOutputBufferInfo.offset = " + audioDecoderOutputBufferInfo.offset + "    audioDecoderOutputBufferInfo.size = " + audioDecoderOutputBufferInfo.size);

                        }
                        mMediaPlayer.audioDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false);

//                        if ((audioDecoderOutputBufferInfo.flags
//                                & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                            Log.d(TAG, "3.播放声音并重新编码声音 audio decoder: EOS");
//                            //TODO audioDecoderDone是否可以在上一个循环中使用BUFFER_FLAG_END_OF_STREAM判断
//                            audioDecoderDone = true;
//                        }

                        break;
                }
                break;
            }// end of while 解码读出的数据
        }
    }

}
