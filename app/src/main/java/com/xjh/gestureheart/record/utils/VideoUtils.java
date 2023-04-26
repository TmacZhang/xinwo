package com.xjh.gestureheart.record.utils;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 25623 on 2018/2/6.
 */

public class VideoUtils {

    private static Calendar mCalendar = Calendar.getInstance();
    private static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMdd-kkmmss-SSS");

    /**
     * @param videosToMerge 需要合并的视频的路径集合
     * @param outputFile   输出的视频
     */
    public static void merge(List<String> videosToMerge, String outputFile){
        int count = videosToMerge.size();
        try {
            Movie[] inMovies = new Movie[count];
            for (int i = 0; i < count; i++) {
                inMovies[i] = MovieCreator.build(videosToMerge.get(i));
            }

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            //提取所有视频和音频的通道
            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                    if (t.getHandler().equals("")) {

                    }
                }
            }

            //添加通道到新的视频里
            Movie result = new Movie();
            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }
            Container mp4file = new DefaultMp4Builder()
                    .build(result);


            //开始生产mp4文件
            FileOutputStream fos =  new FileOutputStream(new File(outputFile));
            FileChannel fco = fos.getChannel();
            mp4file.writeContainer(fco);
            fco.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 为输出文件名生成时间
     *
     * @return
     */
    private static String generateTimeString4OutputName() {
        return "GH-" + mDateFormat.format(mCalendar.getTime());
    }

    /**
     * 生成输出文件名, 如果outputFormat为空，则输出文件使用与输入文件相同的格式
     * @param outputPath
     * @return
     */
    public static String createTempOutputFile4Video(String outputPath, int index) {

        File outputPathFile = new File(outputPath);
        if(!outputPathFile.exists()){
            outputPathFile.mkdirs();
        }

        StringBuilder outputFileStr = new StringBuilder();
        outputFileStr.append(outputPath)
                .append("/")
                .append(generateTimeString4OutputName())
                .append("_idx_")
                .append(index)
                .append(".mp4");

        File outputFile = new File(outputFileStr.toString());
        if(outputFile.exists()){
            outputFile.delete();
        }


        return outputFileStr.toString();
    }

    public static String createOutputFile4Video(String outputPath) {

        File outputPathFile = new File(outputPath);
        if(!outputPathFile.exists()){
            outputPathFile.mkdirs();
        }

        StringBuilder outputFileStr = new StringBuilder();
        outputFileStr.append(outputPath)
                .append("/")
                .append(generateTimeString4OutputName())
                .append(".mp4");

        File outputFile = new File(outputFileStr.toString());
        if(outputFile.exists()){
            outputFile.delete();
        }


        return outputFileStr.toString();
    }

    /**
     * 删除临时视频
     * @param tmpVideoPathList
     */
    public static void deleteTmpVideo(List<String> tmpVideoPathList) {
        if(tmpVideoPathList != null && tmpVideoPathList.size() > 0){
            int count = tmpVideoPathList.size();
            String tmpVideo;
            File tmpVideoFile;
            for(int i=0; i<count; i++){
                tmpVideo = tmpVideoPathList.get(i);
                tmpVideoFile = new File(tmpVideo);
                if(tmpVideoFile != null && tmpVideoFile.exists()){
                    tmpVideoFile.delete();
                }
            }
            tmpVideoPathList.clear();
        }
    }
}
