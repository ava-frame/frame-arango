package com.ava.frame.arango;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.util.MapBuilder;
import com.ava.frame.core.SpringApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by redredava on 2018/7/23.
 * email:zhyx2014@yeah.net
 */
public class ArangoTemplate extends ArangoOperation {
    private List<ArangoFilter> filters = new ArrayList<>();
    private int limit = 10;
    private int page = 1;

    private String key(int filterIndex) {
        return "key" + filterIndex;
    }

    public ArangoTemplate(ArangoClient client) {
        this.client = client;
    }

    public ArangoTemplate(String doc, ArangoClient client) {
        this.doc = doc;
        this.client = client;
    }

    public static ArangoTemplate build(String doc) {
        return new ArangoTemplate(doc, (ArangoClient) SpringApplicationContext.getBean("arangoClient"));
    }

    /**
     * limit offset,count
     *
     * @return
     */
    private String limitString() {
        if (page > 0 && limit > 0)
            return " limit " + ((page - 1) * limit) + "," + limit;
        return "";
    }

    public ArangoTemplate limit(int limit) {
        this.limit = limit;
        return this;
    }
    public ArangoTemplate option(AqlQueryOptions options) {
        this.options = options;
        return this;
    }
    public ArangoTemplate page(int page) {
        this.page = page;
        return this;
    }

    public ArangoTemplate db(int dbIndex) {
        this.dbIndex = dbIndex;
        return this;
    }

    /**
     * 拼接查找
     *
     * @return
     */
    public List find() {
        return find(BaseDocument.class);
    }

    /**
     * 拼接查找
     *
     * @param clazz
     * @return
     */
    public List find(Class clazz) {
        StringBuilder query = new StringBuilder();
        MapBuilder map = new MapBuilder();
        //        table : for o in table
        query.append(" for o in ").append(doc);
//        filter
        if (!filters.isEmpty()) {
            query.append(" filter ");
            int index = 0;
            for (ArangoFilter filter : filters) {
                if (index == 0) {
                    query.append(filter.getFilterString().replace("&&", ""));
                } else {
                    query.append(filter.getFilterString());
                }
                map.put(key(index), filter.getValue());
            }
        }
        //        limit offset,count
        query.append(limitString());
        //        return o
        query.append(" return o ");
        ArangoCursor cursor = client.dbs[dbIndex].query(query.toString(), map.get(), options,clazz);
        List list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add( cursor.next());
        }
        return list;
    }

    public ArangoTemplate eq(String field, Object value) {
        ArangoFilter arangoFilter = new ArangoFilter(field, value);
        arangoFilter.setFilterString(" && o." + field + " ==@" + key(filters.size()));
        filters.add(arangoFilter);
        return this;
    }

    public ArangoTemplate gt(String field, Object value) {
        ArangoFilter arangoFilter = new ArangoFilter(field, value);
        arangoFilter.setFilterString(" && o." + field + " >@" + key(filters.size()));
        filters.add(arangoFilter);
        return this;
    }

    public ArangoTemplate lt(String field, Object value) {
        ArangoFilter arangoFilter = new ArangoFilter(field, value);
        arangoFilter.setFilterString("  && o." + field + " <@" + key(filters.size()));
        filters.add(arangoFilter);
        return this;
    }

    public ArangoTemplate gte(String field, Object value) {
        ArangoFilter arangoFilter = new ArangoFilter(field, value);
        arangoFilter.setFilterString(" && o." + field + " >=@" + key(filters.size()));
        filters.add(arangoFilter);
        return this;
    }

    public ArangoTemplate lte(String field, Object value) {
        ArangoFilter arangoFilter = new ArangoFilter(field, value);
        arangoFilter.setFilterString(" && o." + field + " <=@" + key(filters.size()));
        filters.add(arangoFilter);
        return this;
    }

    public ArangoTemplate contains(String field, Object value) {
        ArangoFilter arangoFilter = new ArangoFilter(field, value);
        arangoFilter.setFilterString(" && contains( o." + field + ",@" + key(filters.size()) + ") ");
        filters.add(arangoFilter);
        return this;
    }

    public ArangoTemplate in(String field, Object value) {
        ArangoFilter arangoFilter = new ArangoFilter(field, value);
        arangoFilter.setFilterString(" && @" + key(filters.size()) + " in o." + field);
        filters.add(arangoFilter);
        return this;
    }

    public ArangoTemplate anyIn(Object value, String[] fields) {
        if (fields == null || fields.length == 0) return this;
        ArangoAnyInFilter arangoFilter = new ArangoAnyInFilter(fields[0], value, fields);
        StringBuilder query = new StringBuilder();
        query.append(" && ");
        int index = 0;
        for (String field : fields) {
            if (index++ == 0) {
                query.append(" o.").append(field);
            } else {
                query.append(" ,o.").append(field);
            }
        }
        query.append(" any in @").append(key(filters.size()));
        arangoFilter.setFilterString(query.toString());
        return this;
    }
}
