package com.wutos.base.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 继续路劲的工具类
 * @author zc
 * @date 2020-02-24
 */
public class ParsePaths {
    public static List<String> parsePathsToList(String paths) {
        List<String> pathsList = new ArrayList<>();
        String[] array = parsePathsToArray(paths);
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null && array[i].length() > 0) {
                pathsList.add(array[i].trim());
            }
        }
        return pathsList;
    }

    public static String[] parsePathsToArray(String paths) {
        if (paths != "" && paths.length() > 0) {
            return paths.split(",");
        }
        return new String[0];
    }
}
