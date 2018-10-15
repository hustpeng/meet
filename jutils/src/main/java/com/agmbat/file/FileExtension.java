/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.file;

/**
 * 常用文件后缀
 */
public class FileExtension {

    /**
     * 默认的未知的
     */
    public static final String UNKNOW = "";

    /**
     * 应用程序类
     */
    public static final String APK = "apk";
    public static final String JS = "js";

    /**
     * 压缩文件类
     */
    public static final String ZIP = "zip";
    public static final String RAR = "rar";
    public static final String GZ = "gz";

    /**
     * 加密文件类型 Encryption
     */
    public static final String ENC = "enc";

    /**
     * 图片类
     */
    public static final String PNG = "png";
    public static final String JPEG = "jpeg";
    public static final String JPG = "jpg";
    public static final String JPE = "jpe";
    public static final String JFIF = "jfif";
    public static final String GIF = "gif";
    public static final String BMP = "bmp";
    public static final String TIF = "tif";
    public static final String TIFF = "tiff";

    /**
     * 音频类
     */
    public static final String MP3 = "mp3";
    public static final String WMA = "wma";
    public static final String WAV = "wav";
    public static final String AAC = "aac";
    public static final String MID = "mid";

    /**
     * 视频类
     */
    public static final String RM = "rm";
    public static final String RMVB = "rmvb";
    public static final String MPG = "mpg";
    public static final String MPEG = "mpeg";
    public static final String DAT = "dat";
    public static final String MOV = "mov";
    public static final String MTV = "mtv";
    public static final String WMV = "wmv";
    public static final String FLV = "flv";
    public static final String E3GP = "3gp";
    public static final String MP4 = "mp4";
    public static final String SWF = "swf";
    public static final String AVI = "avi";
    public static final String MKV = "mkv";

    /**
     * Word 类型
     */
    public static final String DOCX = "docx";
    public static final String DOC = "doc";
    public static final String DOTM = "dotm";
    public static final String DOCM = "docm";
    public static final String DOCT = "doct";
    public static final String DOT = "dot";
    public static final String DOTX = "dotx";
    public static final String VSD = "vsd";

    /**
     * Excel 类型
     */
    public static final String XLSX = "xlsx";
    public static final String XLSM = "xlsm";
    public static final String XLSB = "xlsb";
    public static final String XLS = "xls";

    /**
     * PPT 类型
     */
    public static final String PPTX = "pptx";
    public static final String PPTM = "pptm";
    public static final String PPT = "ppt";

    /**
     * Txt 类型
     */
    public static final String TXT = "txt";

    /**
     * lrc歌词文件后缀
     */
    public static final String LRC = "lrc";

    /**
     * PDF 类型
     */
    public static final String PDF = "pdf";

    /**
     * HTML 类型
     */
    public static final String HTML = "html";
    public static final String HTM = "htm";

    public static boolean isTxtType(String ext) {
        return TXT.equalsIgnoreCase(ext);
    }

    public static boolean isPPTType(String ext) {
        return PPTX.equalsIgnoreCase(ext) || PPTM.equalsIgnoreCase(ext) || PPT.equalsIgnoreCase(ext);
    }

    public static boolean isHtmlType(String ext) {
        return HTML.equalsIgnoreCase(ext) || HTM.equalsIgnoreCase(ext);
    }

    /**
     * 是否为图片类型
     *
     * @param ext
     * @return
     */
    public static boolean isImageType(String ext) {
        return PNG.equalsIgnoreCase(ext) || JPEG.equalsIgnoreCase(ext) || JPG.equalsIgnoreCase(ext)
                || JPE.equalsIgnoreCase(ext) || JFIF.equalsIgnoreCase(ext) || GIF.equalsIgnoreCase(ext)
                || BMP.equalsIgnoreCase(ext) || TIF.equalsIgnoreCase(ext) || TIFF.equalsIgnoreCase(ext);
    }

    /**
     * 是否为音频类型
     *
     * @param ext
     * @return
     */
    public static boolean isAudioType(String ext) {
        return MP3.equalsIgnoreCase(ext) || WMA.equalsIgnoreCase(ext) || WAV.equalsIgnoreCase(ext)
                || AAC.equalsIgnoreCase(ext) || MID.equalsIgnoreCase(ext);
    }

    public static boolean isVideoType(String ext) {
        return RM.equalsIgnoreCase(ext) || RMVB.equalsIgnoreCase(ext) || MPG.equalsIgnoreCase(ext)
                || MPEG.equalsIgnoreCase(ext) || DAT.equalsIgnoreCase(ext) || MOV.equalsIgnoreCase(ext)
                || MTV.equalsIgnoreCase(ext) || WMV.equalsIgnoreCase(ext) || FLV.equalsIgnoreCase(ext)
                || E3GP.equalsIgnoreCase(ext) || MP4.equalsIgnoreCase(ext) || SWF.equalsIgnoreCase(ext)
                || AVI.equalsIgnoreCase(ext) || MKV.equalsIgnoreCase(ext);
    }

    public static boolean isWordType(String ext) {
        return DOCX.equalsIgnoreCase(ext) || DOC.equalsIgnoreCase(ext) || DOTM.equalsIgnoreCase(ext)
                || DOCM.equalsIgnoreCase(ext) || DOCT.equalsIgnoreCase(ext) || DOT.equalsIgnoreCase(ext)
                || DOTX.equalsIgnoreCase(ext) || VSD.equalsIgnoreCase(ext);
    }

    public static boolean isApplicationType(String ext) {
        return APK.equalsIgnoreCase(ext);
    }

    public static boolean isZipType(String ext) {
        return ZIP.equalsIgnoreCase(ext) || RAR.equalsIgnoreCase(ext);
    }

    public static boolean isExcelType(String ext) {
        return XLSX.equalsIgnoreCase(ext) || XLSM.equalsIgnoreCase(ext) || XLSB.equalsIgnoreCase(ext)
                || XLS.equalsIgnoreCase(ext);
    }

    public static boolean isPDFType(String ext) {
        return PDF.equalsIgnoreCase(ext);
    }

    /**
     * 是否为文档类型
     *
     * @param ext
     * @return
     */
    public static boolean isDocumentType(String ext) {
        return isTxtType(ext) || isPDFType(ext) || isWordType(ext) || isExcelType(ext) || isPPTType(ext);
    }
}
