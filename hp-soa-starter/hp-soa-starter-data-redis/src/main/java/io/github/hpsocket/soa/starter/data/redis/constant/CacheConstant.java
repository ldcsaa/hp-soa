package io.github.hpsocket.soa.starter.data.redis.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

/**
 * @author Bruce
 * @date 2026/7/5 18:02
 */
public class CacheConstant
{
    public static final String $5s = "5s";
    public static final String $10s = "10s";
    public static final String $30s = "30s";
    public static final String $1m = "1m";
    public static final String $3m = "3m";
    public static final String $5m = "5m";
    public static final String $10m = "10m";
    public static final String $15m = "15m";
    public static final String $30m = "30m";
    public static final String $1h = "1h";
    public static final String $3h = "3h";
    public static final String $6h = "6h";
    public static final String $12h = "12h";
    public static final String $1d = "1d";
    public static final String $3d = "3d";
    public static final String $7d = "7d";
    public static final String $15d = "15d";
    public static final String $30d = "30d";
    public static final String $60d = "60d";
    public static final String $90d = "90d";
    public static final String $180d = "180d";
    public static final String $1y = "1y";
    public static final String $3y = "3y";

    @Getter
    @AllArgsConstructor
    public enum InitialCache
    {
        $5S($5s, Duration.ofSeconds(5))
        , $10S($10s, Duration.ofSeconds(10))
        , $30S($30s, Duration.ofSeconds(30))
        , $1M($1m, Duration.ofMinutes(1))
        , $3M($3m, Duration.ofMinutes(3))
        , $5M($5m, Duration.ofMinutes(5))
        , $10M($10m, Duration.ofMinutes(10))
        , $15M($15m, Duration.ofMinutes(15))
        , $30M($30m, Duration.ofMinutes(30))
        , $1H($1h, Duration.ofHours(1))
        , $3H($3h, Duration.ofHours(3))
        , $6H($6h, Duration.ofHours(6))
        , $12H($12h, Duration.ofHours(12))
        , $1D($1d, Duration.ofDays(1))
        , $3D($3d, Duration.ofDays(3))
        , $7D($7d, Duration.ofDays(7))
        , $15D($15d, Duration.ofDays(15))
        , $30D($30d, Duration.ofDays(30))
        , $60D($60d, Duration.ofDays(60))
        , $90D($90d, Duration.ofDays(90))
        , $180D($180d, Duration.ofDays(180))
        , $1Y($1y, Duration.ofDays(365))
        , $3Y($3y, Duration.ofDays(365*3))
        ;

        private String name;
        private Duration expire;
    }
}
