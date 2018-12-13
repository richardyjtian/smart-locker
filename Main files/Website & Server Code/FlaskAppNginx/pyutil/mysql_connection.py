#!/usr/bin/python3.6

import pymysql


def connection():
    conn = pymysql.connect(host="localhost",
                           user="root",
                           passwd="nZVLJudP4Qaz",
                           db="291G19P2")
    c = conn.cursor()
    return c, conn
