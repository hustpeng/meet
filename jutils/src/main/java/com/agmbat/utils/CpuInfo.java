/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-07-23
 */
package com.agmbat.utils;

import com.agmbat.file.FileUtils;
import com.agmbat.text.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class to get CPU information. All information are get from /proc/cpuinfo of linux OS.
 */
public final class CpuInfo {

    // CPU family
    public static final int ANDROID_CPU_FAMILY_UNKNOWN = 0;
    public static final int ANDROID_CPU_FAMILY_ARM = 1;
    public static final int ANDROID_CPU_FAMILY_X86 = 2;

    // ARM CPU feature
    public static final int ANDROID_CPU_ARM_FEATURE_ARMV7 = (1 << 0);
    public static final int ANDROID_CPU_ARM_FEATURE_VFPV3 = (1 << 1);
    public static final int ANDROID_CPU_ARM_FEATURE_NEON = (1 << 2);
    public static final int ANDROID_CPU_ARM_FEATURE_LDREX_STREX = (1 << 3);

    /**
     * CPU arch
     */
    private static final String CPU_ARMEABI = "armeabi";
    private static final String CPU_X86 = "x86";
    private static final String CPU_MIPS = "mips";

    private static CpuInfo sCpuInfo;

    private int mCpuFamily = ANDROID_CPU_FAMILY_UNKNOWN;
    private int mCpuFeatures = 0;
    private int mCpuCount = 1;

    /**
     * Cpu名称
     */
    private String mProcessorInfo;

    /**
     * Cpu arch
     */
    private String mCpuArch;

    private String[] mCpuInfoText;

    private CpuInfo() {
        String cpuInfoString = FileUtils.readFileText("/proc/cpuinfo");
        if (StringUtils.isEmpty(cpuInfoString)) {
            return;
        }
        if (cpuInfoString.contains("ARM")) {
            mCpuFamily = ANDROID_CPU_FAMILY_ARM;
        } else {
            // BUGBUG: only support ARM CPU now.
        }
        mCpuInfoText = cpuInfoString.split("\n");

        // Count the CPU cores, the value may be 0 for single-core CPUs.
        mProcessorInfo = extractField("Processor");
        mCpuArch = getCpuArch(mProcessorInfo);
        mCpuCount = countField("processor");
        if (mCpuCount == 0) {
            mCpuCount = 1;
        }

        extractFeatures();
    }

    /**
     * 获取Cpu信息
     *
     * @return
     */
    public static CpuInfo get() {
        if (sCpuInfo == null) {
            sCpuInfo = new CpuInfo();
        }
        return sCpuInfo;
    }

    private static String getCpuArch(String cpuName) {
        String cpuArchitect = CPU_ARMEABI;
        if (cpuName.toLowerCase().contains("arm")) {
            cpuArchitect = CPU_ARMEABI;
        } else if (cpuName.toLowerCase().contains("x86")) {
            cpuArchitect = CPU_X86;
        } else if (cpuName.toLowerCase().contains("mips")) {
            cpuArchitect = CPU_MIPS;
        }
        return cpuArchitect;
    }

    /**
     * Extract CPU features.
     */
    private void extractFeatures() {
        String cpuArch = extractField("CPU architecture");
        if (!StringUtils.isEmpty(cpuArch)) {
            int archNumber;

            // Read the initial decimal number, ignore the rest.
            try {
                // Fix crash bug for arch "6TEJ"
                archNumber = Integer.valueOf(cpuArch);
            } catch (NumberFormatException e) {
                // Can't parse arch number, set to 6
                archNumber = 6;
            }

            boolean hasARMv7 = archNumber >= 7;

            // Unfortunately, it seems that certain ARMv6-based CPUs
            // report an incorrect architecture number of 7!
            //
            // See http://code.google.com/p/android/issues/detail?id=10812
            //
            // We try to correct this by looking at the 'elf_format'
            // field reported by the 'Processor' field, which is of the
            // form of "(v7l)" for an ARMv7-based CPU, and "(v6l)" for
            // an ARMv6-one.
            if (hasARMv7) {
                if (!StringUtils.isEmpty(mProcessorInfo) && mProcessorInfo.contains("(v6l)")) {
                    hasARMv7 = false;
                }
            }

            if (hasARMv7) {
                mCpuFeatures |= ANDROID_CPU_ARM_FEATURE_ARMV7;
            }

            // The LDREX / STREX instructions are available from ARMv6.
            if (archNumber >= 6) {
                mCpuFeatures |= ANDROID_CPU_ARM_FEATURE_LDREX_STREX;
            }

            // Extract the list of CPU features from 'Features' field.
            String cpuFeatures = extractField("Features");
            if (!StringUtils.isEmpty(cpuFeatures)) {
                List<String> list = Arrays.asList(cpuFeatures.split(" "));
                if (list.contains("vfpv3") || list.contains("vfpv3d16")) {
                    mCpuFeatures |= ANDROID_CPU_ARM_FEATURE_VFPV3;
                }
                if (list.contains("neon")) {
                    // Note: Certain kernels only report neon but not vfpv3 in
                    // their features list. However, ARM mandates that if Neon
                    // is implemented, so must be VFPv3 so always set the flag.
                    mCpuFeatures |= ANDROID_CPU_ARM_FEATURE_NEON | ANDROID_CPU_ARM_FEATURE_VFPV3;
                }
            }
        }
    }

    /**
     * Returns family of the CPU.
     */
    public int getFamily() {
        return mCpuFamily;
    }

    /**
     * Returns features of the CPU. The return value is CPU family specific.
     */
    public int getFeatures() {
        return mCpuFeatures;
    }

    /**
     * Returns number of CPU cores.
     */
    public int getCount() {
        return mCpuCount;
    }

    public String getProcessorInfo() {
        return mProcessorInfo;
    }

    public String getCpuArch() {
        return mCpuArch;
    }

    /**
     * 获取第一个对应的field的值
     *
     * @param field
     * @return
     */
    private String extractField(String field) {
        String ret = "";
        for (String line : mCpuInfoText) {
            // The field is at the start of a line, followed by any whitespace and then a colon.
            if (line.startsWith(field)) {
                // Skip to the first column followed by a space.
                String[] result = line.split(": ");
                if (result.length > 1) {
                    ret = result[1];
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * 计算field个数
     *
     * @param field
     * @return
     */
    private int countField(String field) {
        int count = 0;
        for (String line : mCpuInfoText) {
            if (line.startsWith(field)) {
                count++;
            }
        }
        return count;
    }

}
