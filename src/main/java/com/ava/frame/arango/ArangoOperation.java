package com.ava.frame.arango;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.util.MapBuilder;
import com.ava.frame.core.SpringApplicationContext;

import java.util.Map;

/**
 * Created by redredava on 2018/7/23.
 * email:zhyx2014@yeah.net
 */
public class ArangoOperation{
    protected int dbIndex = 0;
    protected ArangoClient client;
    protected String doc;
    protected AqlQueryOptions options;
    public void insert(BaseDocument insertDoc) {
        String insertCmmd = "insert @insertDoc into @@doc";
        client.dbs[dbIndex].query(insertCmmd, new MapBuilder().put("insertDoc", insertDoc).put("@doc", doc).get(), null, null);
    }

    public void update( String key, BaseDocument updateDoc) {
        String updateCmmd = "update {_key:@key} with @updateDoc into @@doc";
        client.dbs[dbIndex].query(updateCmmd, new MapBuilder().put("key", key).put("updateDoc", updateDoc).put("@doc", doc).get(), null, null);
    }

    /**
     * @param upsertDoc search condition
     * @param insertDoc
     * @param updateDoc
     */
    public void upsert( BaseDocument upsertDoc, BaseDocument insertDoc, BaseDocument updateDoc) {
        String upsertCmmd = "upsert @upsertDoc insert @insertDoc update @updateDoc in @doc  OPTIONS { keepNull: false }";
        client.dbs[dbIndex].query(upsertCmmd, new MapBuilder().put("upsertDoc", upsertDoc).put("insertDoc", insertDoc)
                .put("updateDoc", updateDoc).put("doc", doc).get(), null, null);
    }

    public ArangoCursor<BaseDocument> query(String query, Map<String, Object> params) {
        return client.dbs[dbIndex].query(query, params, options, BaseDocument.class);
    }



}
