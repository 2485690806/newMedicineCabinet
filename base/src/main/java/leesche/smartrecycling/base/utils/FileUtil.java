package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.leesche.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import leesche.smartrecycling.base.common.Constants;

public class FileUtil {
    /**
     * 输入流复制到目标文件
     */
    public static void copyStream(InputStream inputStream, File destFile) throws Exception {
        if (inputStream == null || destFile == null) {
            throw new IllegalArgumentException("InputStream or destFile is null");
        }

        OutputStream outputStream = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024 * 4];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }

        // 关闭流
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }
    /**
     * 创建文件夹
     *
     * @return File
     */
    public static File createDir(String path) {
        File dir = new File(path);
        if (!isExist(dir)) {
            dir.mkdirs();
        }
        return dir;
    }

    public static List<String> getFilePaths(String fileAbsolutePath) {
        List<String> vecFile = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        if (!file.exists()) {
            return vecFile;
        }
        File[] subFile = file.listFiles();
        for (File value : subFile) {
            if (!value.isDirectory()) {
                String path = value.getAbsolutePath();
//                if (path.endsWith("mp4") || path.endsWith("jpg") || path.endsWith("zip"))
                vecFile.add(path);
            }
        }
        return vecFile;
    }

    public static List<String> getFilePaths(String fileAbsolutePath, String typeFlag) {
        List<String> vecFile = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String path = subFile[iFileLength].getAbsolutePath();
                if (path.endsWith(typeFlag)) vecFile.add(path);
            }
        }
        return vecFile;
    }

    public static List<String> getDirToDelete(String fileAbsolutePath, int _day) {
        List<String> vecFile = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if (subFile != null && subFile.length > 0) {
            for (File file1 : subFile) {
                if (file1.getName().equals("compress")) continue;
                int day = DateUtil.getDiffFormToday(file1.getName());
                if (day > _day) {
                    vecFile.add(file1.getName());
                }
            }
        }
        return vecFile;
    }

    /**
     * 判断当前文件是否存在
     *
     * @param file
     * @return
     */
    public static boolean isExist(File file) {
        return file.exists();
    }

    //写数据到SD中的文件
    public static void writeFileSdcardFile(String fileName, String write_str) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(fileName);
            byte[] bytes = write_str.getBytes();
            fout.write(bytes);
            fout.flush();
            fout.getFD().sync();//将数据同步到达物理存储设备
            fout.close();
        } catch (IOException e) {
            if (fout != null) {
                try {
                    fout.flush();
                    fout.getFD().sync();//将数据同步到达物理存储设备
                    fout.close();
                } catch (IOException e1) { /* fail silently */ }
            }
        }
    }

    //读SD中的文件
    public static String readFileSdcardFile(String fileName) {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param dest   目标文件
     * @throws IOException
     */
    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        String filenameTemp = dest.getPath() + "/SmartRecycling" + ".apk";
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            if (!dest.exists()) {
                try {
                    //按照指定的路径创建文件夹
                    dest.mkdirs();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            File dir = new File(filenameTemp);
            if (!dir.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    dir.createNewFile();
                } catch (Exception e) {
                }
            }
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(filenameTemp).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    // 判断文件是否存在
    public static boolean judeFileExists(File file) {
        return file.exists();
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath, boolean isDelete) {
        if (!new File(filePath).exists()) return false;
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (files == null || files.length == 0) {
            if (isDelete) {
                return dirFile.delete();
            }
            return false;
        }
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
//                Logger.i("del file path：" + file.getAbsolutePath());
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file.getAbsolutePath(), isDelete);
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        if (!isDelete) return true;
        return dirFile.delete();
    }

    public static void deleteDirAFewDaysAgo(int days) {
        File dirFile = new File(Constants.FFMPEG_RECORD_VIDEO);
        if (!dirFile.exists()) return;
        File[] files = dirFile.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                int day = DateUtil.getDiff2FormToday(file.getName(), DateUtil.DATE_FORMAT3);
                if (day > days) {
                    Logger.i("[系统]文件删除: " + file.getAbsolutePath());
                    deleteDirectory(file.getAbsolutePath(), true);
                }
            }
        }
    }

    public static boolean delete2Directory(String filePath, String filerImg) {
        if (!new File(filePath).exists()) return false;
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (files == null || files.length == 0) return true;
        for (File file : files) {
            // 删除子文件
            if (file.isFile() && !file.getAbsolutePath().contains(filerImg)) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = delete2Directory(file.getAbsolutePath(), filerImg);
                if (!flag)
                    break;
            }
        }
        return flag;
    }

    public static boolean delete3Directory(String filePath, String[] filerFiles) {
        if (!new File(filePath).exists()) return false;
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        if (files == null || files.length == 0) return true;
        for (File file : files) {
            if (file.isFile()) {
                boolean isDelete = true;
                for (String mark : filerFiles) {
                    if (file.getAbsolutePath().contains(mark)) {
                        isDelete = false;
                        break;
                    }
                }
                if (!isDelete) continue;
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            } else if (file.isDirectory()) {
                flag = delete3Directory(file.getAbsolutePath(), filerFiles);
                if (!flag)
                    break;
            }
        }
        return flag;
    }

    /**
     * 删除单个文件
     *
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        if (file.exists() && file.isFile()) {
//            Logger.i("【Del File】" + filePath$Name);
            return file.delete();
        } else {
            return false;
        }
    }


    public static boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean rename(final File file, final String newName) {
        // 文件为空返回false
        if (file == null) return false;
        // 文件不存在返回false
        if (!file.exists()) return false;
        // 新的文件名为空返回false
        if (isSpace(newName)) return false;
        // 如果文件名没有改变返回true
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        // 如果重命名的文件已存在返回false
        return !newFile.exists()
                && file.renameTo(newFile);
    }

    public static String rename2(final File file, final String newName) {
        String newFileName = file.getParent() + File.separator + newName;
        File newFile = new File(newFileName);
        // 如果重命名的文件已存在返回false
        if (!newFile.exists()) {
            if (file.renameTo(newFile)) return newFileName;
        } else {
            newFile.delete();
            newFile = new File(newFileName);
            if (file.renameTo(newFile)) return newFileName;
        }
        return newFileName;
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String saveBitmap(String userFlag, Bitmap mBitmap) {
        String savePath = Constants.BASE_CACHE_DIR + File.separator + "Ai" + File.separator + userFlag;
        File filePic;
        try {
            filePic = new File(savePath + File.separator + "easyDl" + DateUtil.getTimeByFormat(DateUtil.FORMAT_ORDERID) + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.i("【Pic Save】", " Path: " + filePic.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }

    public static String saveBitmapKR(String savePath, Bitmap mBitmap) {
        File filePic;
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.i("【Pic Save】", " Path: " + filePic.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }

    public static void zipFolder(String srcFileString, String zipFileString) {
        //创建ZIP
        try {
            ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
            //创建文件
            File file = new File(srcFileString);
            //压缩
            ZipFiles(file.getParent() + File.separator, file.getName(), outZip);
            //完成和关闭
            outZip.finish();
            outZip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) {
        try {
            if (zipOutputSteam == null)
                return;
            File file = new File(folderString + fileString);
            if (file.isFile()) {
                ZipEntry zipEntry = new ZipEntry(fileString);
                FileInputStream inputStream = new FileInputStream(file);
                zipOutputSteam.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[4096];
                while ((len = inputStream.read(buffer)) != -1) {
                    zipOutputSteam.write(buffer, 0, len);
                }
                FileUtil.deleteSingleFile(file.getAbsolutePath());
                zipOutputSteam.closeEntry();
            } else {
                //文件夹
                String[] fileList = file.list();
                //没有子文件和压缩
                if (fileList.length <= 0) {
                    ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                    zipOutputSteam.putNextEntry(zipEntry);
                    zipOutputSteam.closeEntry();
                }
                //子文件和递归
                for (String s : fileList) {
                    ZipFiles(folderString + fileString + "/", s, zipOutputSteam);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap bitmap = null;
        AssetManager assetManager = context.getAssets();
        try {
            InputStream is = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            data = new byte[is.available()];
            is.read(data);
            result = Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
