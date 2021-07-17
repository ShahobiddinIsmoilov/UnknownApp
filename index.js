// Set up a server

const { query } = require('express');
const express = require('express');
const PORT = process.env.PORT || 3000;
const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended : true }));

app.listen(PORT, function() {
    console.log(`Server is running on port, ${PORT}.`);
})


// Establish connection to mongoDB

const { MongoClient } = require('mongodb');
const CONNECTION_URL = "mongodb+srv://RandomUser:NYRjkAaMbCmzl4Aq@randomcluster.klfjq.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
const client = new MongoClient(CONNECTION_URL, { useUnifiedTopology: true });
client.connect();


// Receive request to Home Page

app.get('/', (req, res) => {
    res.send("HALF-LIFE 2 IS THE BEST GAME EVER. PERIOD.");
});


// 


// Status request

app.get('/status', (req, res) => {
    let query = req.query;
    const cargo = findSimilar({_id: query.groupID});
    cargo.then((value_gs) => {
        if(value_gs.found === true) {
            let array = value_gs.list, k = -1;
            for (let i = 0; i < array.length; i++) {
                if (array[i].phoneNumber == query.phoneNumber) {
                    k = i;
                    break;
                }
            };
            if (k > -1) {
                res.json({
                    ok: "true",
                    cleanables: array[k].cleanables,
                    uncleanables: array[k].uncleanables,
                    message: array[k].message
                });
            }
            else res.json({
                ok: "false",
                errCode: "no_student_found"
            });
        }
        else res.json({
            ok: "false",
            errCode: "no_group_found"
        });
    }).catch((err) => {
        res.json({
            ok: "false",
            errCode: err.Code
        });
    });
});


// Recieve request to get group info

app.get('/groupInfo', (req, res) => {
    let query = req.query;
    const cargo = findSimilar({ _id: query.groupID });
    cargo.then((value_gs) => {
        res.json({
            ok: "true",
            groupID: value_gs.groupID,
            code: value_gs.code,
            level: value_gs.level,
            days: value_gs.days,
            time: value_gs.time,
            teacher: value_gs.teacher,
            date: value_gs.date,
            cashier: value_gs.cashier,
	    cashierName: value_gs.cashierName,
            students: value_gs.list
        });
    }).catch((err) => {
        res.json({
            ok: "false",
            errCode: err.errorCode
        });
    });
});


// Receive request to get students list

app.get('/students', (req, res) => {
    let query = req.query;
    const cargo = findSimilar({ _id: query.groupID });
    cargo.then((value_gs) => {
        res.json({
            ok: "true",
            students: value_gs.list
        });
    }).catch((err) => {
        res.json({
            ok: "false",
            errCode: err.errorCode
        });
    });
});


// Receive request to change name

app.get('/changeName', (req, res) =>  {
    let query = req.query;
    changeName(query.phoneNumber, query.name).then(() => {
        res.json({
            ok: "true"
        });
    }).catch(() => {
        res.json({
            ok: "false",
            errCode: "operation_error"
        });
    });
});


// Change name

async function changeName(phoneNumber, newName) {
    const result = await client.db("RandomData").collection("RandomCollection").updateOne(
        {
            "students.phoneNumber": phoneNumber
        },
        {
            $set: {
                "students.$.name": newName
            }
        }
    );
}


// Receive request to update status

app.get('/update', (req, res) => {
    let query = req.query;
    updateStatus(query.phoneNumber, query.cln, query.uncln, query.msg).then(() => {
        res.json({
            ok: "true",
        });
    }).catch(() => {
        res.json({
            ok: "false",
            errCode: "operation_error"
        });
    });
});


// Update status

async function updateStatus(phoneNumber, cleanables, uncleanables, message) {
    const result = await client.db("RandomData").collection("RandomCollection").updateOne(
        {
            "students.phoneNumber": phoneNumber
        },
        { 
            $set: {
                "students.$.cleanables": cleanables,
                "students.$.uncleanables": uncleanables,
                "students.$.message": message
            }
        }
    );
}


// Receive request to appoint a new cashier

app.get('/setCashier', (req, res) =>  {
    let query = req.query;
    setCashier(query.groupID, query.phoneNumber, query.cashierName).then(() => {
        res.json({
            ok: "true"
        });
    }).catch(() => {
        res.json({
            ok: "false",
            errCode: "operation_error"
        });
    });
});


// Appoint new cashier

async function setCashier(groupID, phoneNumber, cashierName) {
    const result = await client.db("RandomData").collection("RandomCollection").updateMany(
        {
            "_id": groupID
        },
        {
            $set: {
                "cashier": phoneNumber,
                "password": "null",
                "cashierName": cashierName
            }
        }
    );
}


// Receive request to change password

app.get('/changePassword', (req, res) =>  {
    let query = req.query;
    updatePassword(query.groupID, query.password).then(() => {
        res.json({
            ok: "true"
        });
    }).catch(() => {
        res.json({
            ok: "false",
            errCode: "operation_error"
        });
    });
});


// Change password

async function updatePassword(groupID, password) {
    const result = await client.db("RandomData").collection("RandomCollection").updateOne(
        {
            "_id": groupID
        },
        {
            $set: {
                "password": password
            }
        }
    );
}


// Receive request to check group's existence

app.get('/checkGroup', (req, res) => {
    let query = req.query;
    const ID = buildID(query.level, query.days, query.time, query.teacher, query.date);
    const cargo = findSimilar({_id: ID});
    cargo.then((value) => {
        if (value.found) {
            res.json({
                ok: "true",
                found: "true"
            });
        }
        else {
            res.json({
                ok: "true",
                found: "false"
            });
        }
    }).catch((err) => {
        res.json({
            ok: "false",
            errCode: err.errorCode
        });
    });
})


// Receive request to check group's existence by ID

app.get('/checkGroupID', (req, res) => {
    let query = req.query;
    const cargo = findSimilar({_id: query.groupID});
    cargo.then((value) => {
        if (value.found) {
            res.json({
                ok: "true",
                found: "true"
            });
        }
        else {
            res.json({
                ok: "true",
                found: "false"
            });
        }
    }).catch((err) => {
        res.json({
            ok: "false",
            errCode: err.errorCode
        });
    });
})


// Receive request to create group

app.get('/addGroup', (req, res) => {
    let query = req.query;
    const ID = buildID(query.level, query.days, query.time, query.teacher, query.date);
    const CODE = buildCode(6);
    insertGroup({
        _id: ID,
        code: CODE,
        level: query.level,
        days: query.days,
        time: query.time,
        teacher: query.teacher,
        date: query.date,
        cashier: query.phoneNumber,
        password: query.password,
        cashierName: query.cashierName,
        students: [
            {
                phoneNumber: query.phoneNumber,
                name: query.name,
                cleanables: 0,
                uncleanables: 0,
                message: ""
            }
        ]
    }).then(() => {
        res.json({
            ok: "true",
            groupID: ID
        })
    }).catch(() => {
        res.json({
            ok: "false",
            errCode: "operation_error"
        });
    });
});


// Insert new group to mongoDB

async function insertGroup(newGroup) {
    const result = await client.db("RandomData").collection("RandomCollection").insertOne(newGroup);
}


// Recieve request to delete group

app.get('/deleteGroup', (req, res) => {
    let query = req.query;
    deleteGroup(query.groupID).then(() => {
        res.json({
            ok: "true"
        })
    }).catch(() => {
        res.json({
            ok: "false",
            errCode: "operation_error"
        });
    });
});


// Remove group from mongoDB

async function deleteGroup(groupID) {
    const result = await client.db("RandomData").collection("RandomCollection").removeOne(
        { "_id": groupID}
    );
}


// Receive request to check the student

app.get('/checkStudent', (req, res) => {
    let query = req.query;
    const cargo = findStudent(query.phoneNumber);
    cargo.then((value) => {
        if (value.found) {
            res.json({
                ok: "true",
                found: "true",
                groupID: value.groupID,
                level: value.level,
                days: value.days,
                time: value.time,
                teacher: value.teacher,
                date: value.date,
                cashier: value.cashier,
                password: value.password
            });
        }
        else {
            res.json({
                ok: "true",
                found: "false"
            });
        }
    }).catch((err) => {
        res.json({
            ok: "false",
            errCode: err.errorCode
        });
    });
})


// Receive request to check the code

app.get('/checkCode', (req, res) => {
    let query = req.query;
    const cargo = findSimilar({code: query.code});
    cargo.then((value) => {
        if (value.found) {
            res.json({
                ok: "true",
                found: "true",
                groupID: value.groupID,
                cashier: value.cashier
            });
        }
        else {
            res.json({
                ok: "true",
                found: "false"
            });
        }
    }).catch((err) => {
        res.json({
            ok: "false",
            errCode: err.errorCode
        });
    });
})


// Recieve request to add student

app.get('/joinGroup', (req, res) => {
    let query = req.query;
    insertStudent(query.code, {
        phoneNumber: query.phoneNumber,
        name: query.name,
        cleanables: 0,
        uncleanables: 0,
        message: ""
    }).then(() => {
        res.json({
            ok: "true"
        });
    }).catch(() => {
        res.json({
            ok: "false",
            errCode: "operation_error"
        });
    });
});


// Insert new student to a group

async function insertStudent(invite_code, newStudent) {
    const result = await client.db("RandomData").collection("RandomCollection").updateOne(
        { "code": invite_code }, { $push: { "students": newStudent } }
    );
}


// Recieve request to remove student

app.get('/leaveGroup', (req, res) => {
    let query = req.query;
    removeStudent(query.groupID, {
        phoneNumber: query.phoneNumber
    }).then(() => {
        res.json({
            ok: "true"
        })
    }).catch(() => {
        res.json({
            ok: "false",
            errCode: "operation_error"
        });
    });
});


// Remove student from a group

async function removeStudent(groupID, student) {
    const result = await client.db("RandomData").collection("RandomCollection").updateOne(
        { "_id": groupID}, { $pull: { "students": student}}
    );
}


// Search similar

function findSimilar(value) {
    return new Promise((resolve, reject) => {
        (client.db("RandomData").collection("RandomCollection").findOne(value)).then((data) => {
            if (data)
                resolve({
                    found: true,
                    groupID: data._id,
                    code: data.code,
                    level: data.level,
                    days: data.days,
                    time: data.time,
                    teacher: data.teacher,
                    date: data.date,
                    cashier: data.cashier,
		    cashierName: data.cashierName,
		    password: data.password,
                    list: data.students
                });
            else
                resolve({found: false});
        }).catch(err => {
            reject({errorCode: "operation_error"});
        });
    }).catch(err => {
        reject({errorCode: "operation_error"});
    });
}


// Search for a student

function findStudent(studentID) {
    return new Promise((resolve, reject) => {
        (client.db("RandomData").collection("RandomCollection").findOne( {
            students: { $elemMatch: {phoneNumber: studentID}}
        })).then((data) => {
            if (data)
                resolve({
                    found: true,
                    groupID: data._id,
                    code: data.code,
                    level: data.level,
                    days: data.days,
                    time: data.time,
                    teacher: data.teacher,
                    date: data.date,
                    cashier: data.cashier,
                    password: data.password,
                    list: data.students
                });
            else
                resolve({found: false});
        }).catch(err => {
            reject({errorCode: "operation_error"});
        });
    }).catch(err => {
        reject({errorCode: "operation_error"});
    });
}


// Generate custom group ID

function buildID(gLevel, gDays, gTime, gTeacher, gDate) {
    //gl4ts0305pShahlo_d0621
    var ultimate = 'gl' + gLevel + 't' + gDays + gTime + 'p' + gTeacher + '_d' + gDate;
    var ultimateID = ultimate.toString();
    return ultimateID;
}


// Generate invite link

function buildCode(length) {
    var result           = '';
    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < length; i++ ) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}