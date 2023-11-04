/*
 Navicat Premium Data Transfer

 Source Server         : docker-mongodb
 Source Server Type    : MongoDB
 Source Server Version : 60010 (6.0.10)
 Source Host           : 192.168.56.23:27017
 Source Schema         : demo

 Target Server Type    : MongoDB
 Target Server Version : 60010 (6.0.10)
 File Encoding         : 65001

 Date: 03/11/2023 19:17:34
*/


// ----------------------------
// Collection structure for employee_info
// ----------------------------
db.getCollection("employee_info").drop();
db.createCollection("employee_info");
db.getCollection("employee_info").createIndex({
    updateTime: NumberInt("1")
}, {
    name: "update time"
});
db.getCollection("employee_info").createIndex({
    jobNumber: NumberInt("1")
}, {
    name: "job number",
    unique: true
});
