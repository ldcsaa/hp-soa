/*
 Navicat Premium Data Transfer

 Source Server         : root@192.168.56.23 - mongodb
 Source Server Type    : MongoDB
 Source Server Version : 80003 (8.0.3)
 Source Host           : 192.168.56.23:27017
 Source Schema         : demo

 Target Server Type    : MongoDB
 Target Server Version : 80003 (8.0.3)
 File Encoding         : 65001

 Date: 23/11/2024 01:48:25
*/


// ----------------------------
// Collection structure for employee_history
// ----------------------------
db.getCollection("employee_history").drop();
db.createCollection("employee_history");

// ----------------------------
// Documents of employee_history
// ----------------------------
db.getCollection("employee_history").insert([ {
    _id: ObjectId("6740c339db9ace4a9eac78b6"),
    jobNumber: "000003",
    name: "大聪明",
    photoUri: "http://images.myserver.com/000003.jpg",
    birthday: ISODate("1985-06-18T16:00:00.000Z"),
    salary: NumberInt("24000"),
    resign: true,
    department: {
        number: "D002",
        name: "公关部"
    },
    updateTime: ISODate("2024-11-22T17:45:29.579Z"),
    _class: "io.github.hpsocket.demo.infra.mongodb.document.EmployeeHistory"
} ]);

// ----------------------------
// Collection structure for employee_info
// ----------------------------
db.getCollection("employee_info").drop();
db.createCollection("employee_info");

// ----------------------------
// Documents of employee_info
// ----------------------------
db.getCollection("employee_info").insert([ {
    _id: "000003",
    jobNumber: "000003",
    name: "大聪明",
    photoUri: "http://images.myserver.com/000003.jpg",
    birthday: ISODate("1985-06-18T16:00:00.000Z"),
    salary: NumberInt("24000"),
    resign: true,
    department: {
        number: "D002",
        name: "公关部"
    },
    updateTime: ISODate("2024-11-22T17:45:29.51Z"),
    _class: "io.github.hpsocket.demo.infra.mongodb.document.EmployeeInfo"
} ]);
