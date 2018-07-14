package com.agmbat.android.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.agmbat.file.FileUtils;
import com.agmbat.text.StringUtils;
import com.agmbat.utils.MathUtils;

/**
 * 图片工具类, 处理图片相关的业务
 */
public class ImageUtils {

    /**
     * 计算同样大小两张图片的相似度
     *
     * @param b1
     * @param b2
     * @return
     */
    public static double getSimilarity(Bitmap b1, Bitmap b2) {
        if (b1.getWidth() != b2.getWidth()) {
            return 0;
        }
        if (b1.getHeight() != b2.getHeight()) {
            return 0;
        }
        int width = b1.getWidth();
        int height = b1.getHeight();
        int numDiffPixels = 0;
        // Now, go through pixel-by-pixel and check that the images are the same;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (ColorUtils.getRGB(b1.getPixel(x, y)) != ColorUtils.getRGB(b2.getPixel(x, y))) {
                    numDiffPixels++;
                }
            }
        }
        double numberPixels = (height * width);
        double diffPercent = numDiffPixels / numberPixels;
        double percent = 1.0 - diffPercent;
        return percent;
    }

    /**
     * 计算不同大小两张图片的相似度
     *
     * @param b1
     * @param b2
     * @return
     */
    public static int getDifference(Bitmap b1, Bitmap b2) {
        String h1 = produceFingerPrint(b1);
        String h2 = produceFingerPrint(b2);
        // 计算"汉明距离"（Hamming distance） 如果不相同的数据位不超过5，就说明两张图片很相似；
        // 如果大于10，就说明这是两张不同的图片。
        int difference = 0;
        int len = h1.length();
        for (int i = 0; i < len; i++) {
            if (h1.charAt(i) != h2.charAt(i)) {
                difference++;
            }
        }
        return difference;
    }

    /**
     * 生成图片指纹
     *
     * @param source 图片
     * @return 图片指纹
     */
    private static String produceFingerPrint(Bitmap source) {
        int width = 8;
        int height = 8;

        // 第一步，缩小尺寸。
        // 将图片缩小到8x8的尺寸，总共64个像素。这一步的作用是去除图片的细节，只保留结构、明暗等基本信息，摒弃不同尺寸、比例带来的图片差异。
        Bitmap thumb = BitmapUtils.resizeBitmap(source, width, height);

        // 第二步，简化色彩。
        // 将缩小后的图片，转为64级灰度。也就是说，所有像素点总共只有64种颜色。
        int[] pixels = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i * height + j] = ColorUtils.rgbToGray(thumb.getPixel(i, j));
            }
        }

        // 第三步，计算平均值。
        // 计算所有64个像素的灰度平均值。
        int avgPixel = MathUtils.average(pixels);

        // 第四步，比较像素的灰度。
        // 将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为1；小于平均值，记为0。
        int[] comps = new int[width * height];
        for (int i = 0; i < comps.length; i++) {
            if (pixels[i] >= avgPixel) {
                comps[i] = 1;
            } else {
                comps[i] = 0;
            }
        }

        // 第五步，计算哈希值。
        // 将上一步的比较结果，组合在一起，就构成了一个64位的整数，这就是这张图片的指纹。组合的次序并不重要，只要保证所有图片都采用同样次序就行了。
        StringBuffer hashCode = new StringBuffer();
        for (int i = 0; i < comps.length; i += 4) {
            int result =
                    comps[i] * (int) Math.pow(2, 3) + comps[i + 1] * (int) Math.pow(2, 2) + comps[i + 2]
                            * (int) Math.pow(2, 1) + comps[i + 2];
            hashCode.append(StringUtils.binaryToHex(result));
        }
        // 得到指纹以后，就可以对比不同的图片，看看64位中有多少位是不一样的。
        return hashCode.toString();
    }

    /**
     * 将图片缩放, 并输出到指定位置
     * 如果图片size 小于指定大小则不处理缩放操作
     *
     * @param source
     * @param outPath
     * @param outWidth
     * @param outHeight
     */
    public static void resizeImage(String source, String outPath, int outWidth, int outHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source, opts);
        if (opts.outWidth <= outWidth && opts.outHeight <= outHeight) {
            FileUtils.copyFile(source, outPath);
            return;
        }
        Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(source, outWidth, outHeight, Bitmap.Config.ARGB_8888, false);
        FileUtils.delete(outPath);
        BitmapUtils.compressToFile(bitmap, outPath);
    }
}
