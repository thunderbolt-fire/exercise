package com.example.exercise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类，用于管理用户数据的SQLite数据库。
 * 负责数据库的创建、升级以及用户表的增、查操作。
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {

    // 定义数据库的名称
    private static final String DATABASE_NAME = "user.db";
    // 定义数据库的版本号。当数据库结构发生变化时，需要增加此版本号。
    private static final int DATABASE_VERSION = 3; // 版本号增加到3，以强制使用新的onCreate逻辑重新创建数据库

    // 定义用户表的名称
    private static final String TABLE_USER = "user";
    // 定义用户表中的列名：邮箱（主键）
    private static final String COLUMN_EMAIL = "email";
    // 定义用户表中的列名：密码
    private static final String COLUMN_PASSWORD = "password";

    /**
     * 构造函数。
     * @param context 上下文对象，用于访问应用环境。
     */
    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 当数据库首次被创建时调用此方法。
     * 在此方法中，我们执行创建用户表的SQL语句，并插入一条默认的用户数据。
     * @param db SQLiteDatabase 对象，代表正在创建的数据库。
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 构建创建用户表的SQL语句
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_EMAIL + " TEXT PRIMARY KEY,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        // 执行SQL语句
        db.execSQL(CREATE_USER_TABLE);

        // 当表被创建时，添加默认用户
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, "test@example.com");
        values.put(COLUMN_PASSWORD, "123456");
        // 插入默认用户数据到用户表
        db.insert(TABLE_USER, null, values);
    }

    /**
     * 当数据库需要升级时调用此方法。
     * 当构造函数中的 DATABASE_VERSION 增加时，此方法会被触发。
     * 这里的升级策略是简单地删除旧表，然后调用onCreate重新创建。
     * @param db         SQLiteDatabase 对象，代表正在升级的数据库。
     * @param oldVersion 旧的数据库版本号。
     * @param newVersion 新的数据库版本号。
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除已存在的用户表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // 重新调用onCreate方法来创建新表和默认用户
        onCreate(db);
    }

    /**
     * 向数据库中添加一个新用户。
     * @param user 要添加的 User 对象。
     */
    public void addUser(User user) {
        // 获取可写的数据库实例
        SQLiteDatabase db = this.getWritableDatabase();
        // 创建一个ContentValues对象，用于存放要插入的数据
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        
        // 使用 insertWithOnConflict 方法插入数据。
        // CONFLICT_IGNORE 参数表示，如果插入的数据导致主键冲突（即用户已存在），则忽略此次插入操作。
        db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        // 关闭数据库连接
        db.close();
    }

    /**
     * 检查数据库中是否存在匹配的邮箱和密码。
     * @param email    用户输入的邮箱。
     * @param password 用户输入的密码。
     * @return 如果邮箱和密码匹配，返回 true；否则返回 false。
     */
    public boolean checkUser(String email, String password) {
        // 获取可读的数据库实例
        SQLiteDatabase db = this.getReadableDatabase();
        // 定义要查询的列
        String[] columns = {COLUMN_EMAIL};
        // 定义查询的条件（WHERE子句）
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        // 定义查询条件中占位符'?'对应的值
        String[] selectionArgs = {email, password};

        // 执行查询
        Cursor cursor = db.query(TABLE_USER, // 表名
                columns,         // 要查询的列
                selection,       // WHERE子句
                selectionArgs,   // WHERE子句的参数
                null,            // groupBy
                null,            // having
                null);           // orderBy

        // 获取查询结果的数量
        int count = cursor.getCount();
        // 关闭Cursor对象，释放资源
        cursor.close();
        // 关闭数据库连接
        db.close();

        // 如果查询到的记录数大于0，说明用户名和密码匹配
        return count > 0;
    }
}
