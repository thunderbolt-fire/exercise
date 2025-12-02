package com.example.exercise.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.exercise.data.model.User;

/**
 * 数据库帮助类，用于管理用户数据的SQLite数据库。
 * <p>
 * 这个类继承自SQLiteOpenHelper，负责数据库的创建、版本升级以及对用户表的增、查等操作。
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {

    // 数据库文件名
    private static final String DATABASE_NAME = "user.db";
    // 数据库版本号。当数据库结构发生变化时（例如增加新表或修改字段），需要增加此版本号。
    private static final int DATABASE_VERSION = 3;

    // 用户表的名称
    private static final String TABLE_USER = "user";
    // 用户表中的列名：邮箱（作为主键）
    private static final String COLUMN_EMAIL = "email";
    // 用户表中的列名：密码
    private static final String COLUMN_PASSWORD = "password";

    /**
     * 构造函数。
     *
     * @param context 上下文对象，用于访问应用环境。
     */
    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 当数据库首次被创建时调用此方法。
     * 在此方法中，我们执行创建用户表的SQL语句，并插入一条默认的测试用户数据。
     *
     * @param db SQLiteDatabase 对象，代表正在创建的数据库。
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 构建创建用户表的SQL语句
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_EMAIL + " TEXT PRIMARY KEY,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        // 执行SQL
        db.execSQL(CREATE_USER_TABLE);

        // 插入默认用户数据
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, "test@example.com");
        values.put(COLUMN_PASSWORD, "123456");
        db.insert(TABLE_USER, null, values);
    }

    /**
     * 当数据库需要升级时调用此方法（即构造函数中的DATABASE_VERSION增加时）。
     * <p>
     * 当前的升级策略非常简单：删除旧的用户表，然后重新创建它。
     * 注意：这是一种破坏性升级，会清除所有已存在的用户数据。在生产环境中，通常需要更复杂的迁移策略。
     *
     * @param db         SQLiteDatabase 对象。
     * @param oldVersion 旧的数据库版本号。
     * @param newVersion 新的数据库版本号。
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除已存在的用户表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // 重新调用onCreate来创建新表和默认用户
        onCreate(db);
    }

    /**
     * 向数据库中添加一个新用户。
     *
     * @param user 要添加的User对象。
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());

        // 使用insertWithOnConflict方法插入数据。CONFLICT_IGNORE表示如果主键冲突（用户已存在），则忽略此次插入。
        db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    /**
     * 检查数据库中是否存在与给定邮箱和密码匹配的用户。
     *
     * @param email    用户输入的邮箱。
     * @param password 用户输入的密码。
     * @return 如果凭据匹配，返回 true；否则返回 false。
     */
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        // 定义要查询的列（这里我们只需要检查是否存在，所以查询任何一列都可以）
        String[] columns = {COLUMN_EMAIL};
        // 定义查询的WHERE条件
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        // 为WHERE条件中的占位符提供值
        String[] selectionArgs = {email, password};

        // 执行查询
        Cursor cursor = db.query(TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null, // groupBy
                null, // having
                null); // orderBy

        // 获取查询结果的行数
        int count = cursor.getCount();
        // 关闭Cursor和数据库连接以释放资源
        cursor.close();
        db.close();

        // 如果行数大于0，说明找到了匹配的用户
        return count > 0;
    }
}
