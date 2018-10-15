package com.agmbat.file;

import java.io.IOException;

public class RootFileOperateUtil {
    /**
     * CMD_GET_ROOT 获取root权限命令
     */
    private static final String CMD_GET_ROOT = "su";

    /**
     * CMD_NEW_FILE 创建新文件命令
     */
    private static final String CMD_NEW_FILE = "touch ";

    /**
     * CMD_NEW_FOLDER 创建新文件夹命令
     */
    private static final String CMD_NEW_FOLDER = "mkdir ";

    /**
     * CMD_DELETE_FILE 删除文件的命令
     */
    private static final String CMD_DELETE_FILE = "rm ";

    /**
     * CMD_DELETE_FOLDER 删除文件夹的命令
     */
    private static final String CMD_DELETE_FOLDER = "rm -r ";

    /**
     * CMD_RENAME_FILE 复制名文件的命令
     */
    private static final String CMD_COPY_FILE = "cp ";

    /**
     * CMD_COPY_FOLDER 复制文件夹的命令
     */
    private static final String CMD_COPY_FOLDER = "cp -r ";

    /**
     * CMD_RENAME_FILE 重命名文件的命令
     */
    private static final String CMD_RENAME_FILE = "mv ";

    /**
     * CMD_RENAME_FOLDER 从命名文件夹的命令
     */
    private static final String CMD_RENAME_FOLDER = "mv ";

    /**
     * @return 判断手机是否已经被root过
     */
    public static boolean isSystemRootted() {
        boolean isRoot = false;
        isRoot = runCMD(CMD_GET_ROOT);
        return isRoot;
    }

    /**
     * 新建一个文件
     *
     * @param filePath 文件的路径
     *                 <p>
     *                 注意请传入文件的绝对路径
     *                 </p>
     * @return 是否创建成功
     */
    public static boolean newFile(String filePath) {
        checkArgNotNull(filePath);
        boolean isSucessfully = false;
        if (getRootPermission()) {
            String cmdString = CMD_NEW_FILE + filePath;
            isSucessfully = runCMD(cmdString);
        }
        return isSucessfully;
    }

    /**
     * 新建一个文件夹
     *
     * @param filePath 文件夹的绝对路径
     *                 <p>
     *                 <h4>注意:</h4>
     *                 <p>
     *                 <p>
     *                 1.请传入文件夹的绝对路径
     *                 </p>
     *                 <p>
     *                 2.保证其父目录都已经存在，不然创建不成功
     *                 </p>
     * @return 是否创建成功
     */
    public static boolean newFolder(String filePath) {
        checkArgNotNull(filePath);
        boolean isSuccessfully = false;
        if (getRootPermission()) {
            String cmdString = CMD_NEW_FOLDER + filePath;
            isSuccessfully = runCMD(cmdString);
        }
        return isSuccessfully;
    }

    /**
     * 删除指定的文件
     *
     * @param filePath 指定文件的绝对路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        checkArgNotNull(filePath);
        boolean isSuccessfully = false;
        if (getRootPermission()) {
            String cmdString = CMD_DELETE_FILE + filePath;
            isSuccessfully = runCMD(cmdString);
        }
        return isSuccessfully;
    }

    /**
     * 删除指定的文件夹
     *
     * @param folderPath 文件夹的路径
     * @return 是否删除成功
     */
    public static boolean deleteFolder(String folderPath) {
        checkArgNotNull(folderPath);
        boolean isSuccessfully = false;
        if (getRootPermission()) {
            String cmdString = CMD_DELETE_FOLDER + folderPath;
            isSuccessfully = runCMD(cmdString);
        }
        return isSuccessfully;
    }

    /**
     * 复制文件
     *
     * @param resFile 源文件绝对路径
     * @param desFile 目标文件路径
     * @return 复制是否成功
     */
    public static boolean copyFile(String resFile, String desFile) {
        checkArgNotNull(resFile);
        checkArgNotNull(desFile);
        boolean isSuccesfully = false;
        if (getRootPermission()) {
            String cmdString =
                    new StringBuilder().append(CMD_COPY_FILE).append(resFile).append(" ").append(desFile).toString();
            isSuccesfully = runCMD(cmdString);
        }
        return isSuccesfully;
    }

    /**
     * 复制文件夹
     *
     * @param resFolder 源文件夹的路径
     * @param desFolder 目标文件夹的路径
     *                  <p>
     *                  请保证其父目录存在
     *                  <p>
     * @return 是否复制成功
     */
    public static boolean copyFolder(String resFolder, String desFolder) {
        checkArgNotNull(resFolder);
        checkArgNotNull(desFolder);
        boolean isSuccesfully = false;
        if (getRootPermission()) {
            String cmdString =
                    new StringBuilder().append(CMD_COPY_FOLDER).append(resFolder).append(" ").append(desFolder)
                            .toString();
            isSuccesfully = runCMD(cmdString);
        }
        return isSuccesfully;
    }

    /**
     * 重命名文件
     *
     * @param filePath    源文件地址
     * @param newFileName 目标文件地址
     *                    <p>
     *                    请保证其路径一定存在
     *                    <p>
     * @return 重命名是否成功
     */
    public static boolean renameFile(String filePath, String newFileName) {
        boolean isSuccessfully = false;
        if (getRootPermission()) {
            String cmdString =
                    new StringBuilder().append(CMD_RENAME_FILE).append(filePath).append(" ").append(newFileName)
                            .toString();
            isSuccessfully = runCMD(cmdString);
        }
        return isSuccessfully;
    }

    /**
     * 重命名文件夹
     *
     * @param filePath    源文件地址
     * @param newFileName 目标文件地址
     *                    <p>
     *                    请保证其路径一定存在
     *                    <p>
     * @return 重命名是否成功
     */
    public static boolean renameFolder(String resFoloder, String desFolder) {
        boolean isSuccessfully = false;
        if (getRootPermission()) {
            String cmdString =
                    new StringBuilder().append(CMD_RENAME_FILE).append(resFoloder).append(" ").append(desFolder)
                            .toString();
            isSuccessfully = runCMD(cmdString);
        }
        return isSuccessfully;
    }

    /**
     * 获取root权限
     *
     * @return 是否获取成功
     */
    private static boolean getRootPermission() {
        return runCMD(CMD_GET_ROOT);
    }

    /**
     * 指令指定的命令
     *
     * @param cmd 命令语句
     * @return 是否执行成功
     */
    private static boolean runCMD(String cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("The cmd can not be null!");
        }
        boolean isRunSuccessfully = false;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(cmd);
            isRunSuccessfully = true;
        } catch (IOException e) {
            isRunSuccessfully = false;
        }
        return isRunSuccessfully;
    }

    /**
     * 检查参数是否为空
     *
     * @param arg
     */
    private static void checkArgNotNull(String arg) {
        if (arg == null) {
            throw new IllegalArgumentException("The arg can not be null!");
        }
    }
}
