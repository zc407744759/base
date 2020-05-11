package com.wutos.base.common.util;

/**
 * 计算两个gps坐标点的距离。
 * @author wangzhe
 * @date 2019/07/01
 */
public class GPSHelper {
    // 赤道半径(单位m)
    private static final double EARTH_RADIUS = 6378137;

    /**
     * 转化为弧度(rad)
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 基于googleMap中的算法得到两经纬度之间的距离,
     * 计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
     *
     * @param lon1 第一点的经度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的经度
     * @param lat3 第二点的纬度
     * @param range 距离值（单位m）
     * @return 返回的是否在指定的距离范围内
     */
    public static boolean isRange(double lon1, double lat1, double lon2, double lat2,double range) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        if (s > range){
            return false;
        }else{
            return true;
        }
    }
}
