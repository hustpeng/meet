package com.agmbat.db;

import android.database.Cursor;

import com.agmbat.db.sql.SqlInfo;
import com.agmbat.db.sql.WhereBuilder;
import com.agmbat.db.table.DbModel;
import com.agmbat.db.table.TableEntity;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * 数据库访问接口
 */
public interface DbManager extends Closeable {

    /**
     * 获取数据库名称
     *
     * @return
     */
    public String getDbName();

    /**
     * 需要确保数据库是唯一性
     *
     * @return
     */
    public String getIdKey();

    /**
     * 保存实体类或实体类的List到数据库, 如果该类型的id是自动生成的, 则保存完后会给id赋值.
     *
     * @param entity
     * @return
     * @throws DbException
     */
    boolean saveBindingId(Object entity) throws DbException;

    /**
     * 保存或更新实体类或实体类的List到数据库, 根据id对应的数据是否存在.
     *
     * @param entity
     * @throws DbException
     */
    void saveOrUpdate(Object entity) throws DbException;

    /**
     * 保存实体类或实体类的List到数据库
     *
     * @param entity
     * @throws DbException
     */
    void save(Object entity) throws DbException;

    /**
     * 保存或更新实体类或实体类的List到数据库, 根据id和其他唯一索引判断数据是否存在.
     *
     * @param entity
     * @throws DbException
     */
    void replace(Object entity) throws DbException;

    // /////////// delete
    void deleteById(Class<?> entityType, Object idValue) throws DbException;

    void delete(Object entity) throws DbException;

    void delete(Class<?> entityType) throws DbException;

    void delete(Class<?> entityType, WhereBuilder whereBuilder) throws DbException;

    // /////////// update
    void update(Object entity, String... updateColumnNames) throws DbException;

    void update(Object entity, WhereBuilder whereBuilder, String... updateColumnNames) throws DbException;

    // /////////// find
    <T> T findById(Class<T> entityType, Object idValue) throws DbException;

    <T> T findFirst(Class<T> entityType) throws DbException;

    <T> List<T> findAll(Class<T> entityType) throws DbException;

    <T> Selector<T> selector(Class<T> entityType) throws DbException;

    DbModel findDbModelFirst(SqlInfo sqlInfo) throws DbException;

    List<DbModel> findDbModelAll(SqlInfo sqlInfo) throws DbException;

    // /////////// table

    /**
     * 删除表
     *
     * @param entityType
     * @throws DbException
     */
    void dropTable(Class<?> entityType) throws DbException;

    /**
     * 添加一列, 新的entityType中必须定义了这个列的属性.
     *
     * @param entityType
     * @param column
     * @throws DbException
     */
    void addColumn(Class<?> entityType, String column) throws DbException;

    /**
     * 删除库
     *
     * @throws DbException
     */
    void dropDb() throws DbException;

    /**
     * 关闭数据库, xUtils对同一个库的链接是单实例的, 一般不需要关闭它.
     *
     * @throws IOException
     */
    void close() throws IOException;

    // /////////// custom
    void execNonQuery(SqlInfo sqlInfo) throws DbException;

    void execNonQuery(String sql) throws DbException;

    Cursor execQuery(SqlInfo sqlInfo) throws DbException;

    Cursor execQuery(String sql) throws DbException;

    /**
     * 构造创建表的sql语句
     *
     * @param tableEntity
     * @param <T>
     * @return
     */
    public <T> String buildCreateTableSql(TableEntity<T> tableEntity);

    public interface DbUpgradeListener {
        public void onUpgrade(DbManager db, int oldVersion, int newVersion);
    }

    /**
     * 判断表是否存在
     *
     * @param entity
     * @return
     * @throws DbException
     */
    public boolean existTable(TableEntity<?> entity) throws DbException;

}
