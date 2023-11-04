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

 Date: 03/11/2023 19:17:42
*/


// ----------------------------
// Collection structure for employee_history
// ----------------------------
db.getCollection("employee_history").drop();
db.createCollection("employee_history");
db.getCollection("employee_history").createIndex({
    updateTime: NumberInt("1")
}, {
    name: "update time",
    expireAfterSeconds: NumberInt("604800")
});
db.getCollection("employee_history").createIndex({
    jobNumber: NumberInt("1")
}, {
    name: "job number"
});
