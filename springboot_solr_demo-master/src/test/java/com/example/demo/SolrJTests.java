package com.example.demo;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * SolrJ 测试
 *
 * @author liangchuanchuan
 */
public class SolrJTests {

    /*
    路径不需要#号,需要注意。
     */
    private String serverUrl = "http://localhost:8080/solr/collection1";

    @Test
    public void addDoc() throws SolrServerException, IOException {
        //构造一篇文档
        SolrInputDocument document = new SolrInputDocument();
        //往doc中添加字段,在客户端这边添加的字段必须在服务端中有过定义
        document.addField("id", 1);
        document.addField("name", "周星星");
        //获得一个solr服务端的请求，去提交,选择具体的某一个solr core，必须存在。
        HttpSolrClient solr = new HttpSolrClient(serverUrl);
        solr.add(document);
        solr.commit();
        solr.close();
    }

    /**
     * 删除索引
     *
     * @throws Exception
     */
    @Test
    public void deleteIndex() throws Exception {
        HttpSolrClient client = new HttpSolrClient(serverUrl);
        //1.删除一个
//        client.deleteById("1001");
        //2.删除多个
//        client.deleteById(Arrays.asList("1001","1002"));
        //3.根据查询条件删除数据,这里的条件只能有一个，不能以逗号相隔
//        client.deleteByQuery("id:1001");
        //4.删除全部，删除不可恢复
        client.deleteByQuery("*:*");
        //一定要记得提交，否则不起作用
        client.commit();
        client.close();
    }

    /**
     * 查询
     *
     * @throws Exception
     */
    @Test
    public void search() throws Exception {
        HttpSolrClient client = new HttpSolrClient(serverUrl);
        //创建查询对象
        SolrQuery query = new SolrQuery();
        //q 查询字符串，如果查询所有*:*
        query.setQuery("name:周星星");
        //start row 分页信息，与mysql的limit的两个参数一致效果
        query.setStart(0);
        query.setRows(10);
        //开启高亮
        query.setHighlight(true);
        //高亮域
        query.addHighlightField("name");
        //前缀
        query.setHighlightSimplePre("<span style='color:red'>");
        //后缀
        query.setHighlightSimplePost("</span>");
        //执行搜索
        QueryResponse queryResponse = client.query(query);
        //搜索结果
        SolrDocumentList results = queryResponse.getResults();
        //查询出来的数量
        System.out.println("总查询出:" + results.getNumFound() + "条记录");
        //遍历搜索记录
        //获取高亮信息
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
        for (SolrDocument solrDocument : results) {
            System.out.println("id:" + solrDocument.get("id"));
            System.out.println("名称 :" + solrDocument.get("name"));
            //输出高亮
            Map<String, List<String>> map = highlighting.get(solrDocument.get("id"));
            List<String> list = map.get("name");
            if (list != null && list.size() > 0) {
                System.out.println("高亮字段：" + list.get(0));
            }
        }
        client.close();
    }

}
