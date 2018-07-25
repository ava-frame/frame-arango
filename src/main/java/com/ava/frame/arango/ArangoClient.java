package com.ava.frame.arango;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by redredava on 2018/7/22.
 * email:zhyx2014@yeah.net
 */
@Component
public class ArangoClient implements InitializingBean{
    @Value("${arango.host}")
    private String host;
    @Value("${arango.port}")
    private int port;
    @Value("${arango.timeout:2000}")
    private int timeout;
    @Value("${arango.username}")
    private String userName;
    @Value("${arango.password}")
    private String password;
    @Value("${arango.dbnames}")
    private String dbNamestr;
    private List<String> dbNames=new ArrayList<>();

    protected ArangoDB arangoDB;
    protected ArangoDatabase[] dbs;

    public void shutDown() {
        arangoDB.shutdown();
    }

    protected void init() {
        //连接Arangodb服务器
        arangoDB = new ArangoDB.Builder().host(host, port).user(userName).password(password).timeout(timeout).build();
        //判断database是否已经存在，不存在就新创建
        dbs = new ArangoDatabase[dbNames.size()];
        //连接Arangodb服务器
        int index = 0;
        for (String dbName : dbNames) {
            dbs[index++] = arangoDB.db(dbName);
        }
    }



    public void setDbNamestr(String dbNamestr) {
        this.dbNamestr = dbNamestr;
        for (String s : this.dbNamestr.split(",")) {
            this.dbNames.add(s);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        setDbNamestr(this.dbNamestr);
        init();
    }
}
