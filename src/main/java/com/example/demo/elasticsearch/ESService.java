package com.example.demo.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.demo.model.ESRequest;
import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.*;

public class ESService {

    private static ElasticsearchClient getClient() {
        // Create the low-level client
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);
        return client;
    }
    public void search() throws IOException {
        ElasticsearchClient client = getClient();
        SearchResponse<Object> search = client.search(s -> s
                        .index("plan")
                        .query(q -> q
                                .term(t -> t
                                        .field("name")
                                        .value(v -> v.stringValue("iphone13"))
                                )),
                Object.class);

        System.out.println("=====");
        System.out.println(search.hits().hits().size());
        for (Hit<Object> hit: search.hits().hits()) {
            processProduct(hit.source());
        }
    }

    public static void get() throws IOException {
        ElasticsearchClient client = getClient();
        GetResponse<Object> get = client.get(s -> {
            GetRequest.Builder b = (GetRequest.Builder) s;
            return b.index("plan").id("plan__12xvxc345ssdsds-508");
        }, Object.class);


        System.out.println("=====");
        System.out.println(get.source());
    }

    /**
     * https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/7.17/loading-json.html
     * @throws IOException
     */
    public static void addDocTest() throws IOException {
        ElasticsearchClient client = getClient();
//        FileReader file = new FileReader(new File("sd", "document-1.json"));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(new Product("ipad", 3000L));
        InputStream targetStream = new ByteArrayInputStream(jsonPayload.getBytes());
        IndexRequest<Object> request = IndexRequest.of(b -> b.index("products").id("abcd").withJson(targetStream));
        client.index(request);
        System.out.println("=====");
    }

    static ElasticsearchClient client = getClient();
    public static void addDoc1() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(new PlanObject(
                "example.com", "12xvxc345ssdsds-508", "plan", "inNetwork", "12-12-2017", "plan"));
        InputStream targetStream = new ByteArrayInputStream(jsonPayload.getBytes());
        IndexRequest<Object> request = IndexRequest.of(b -> b.index("plan").id("plan__12xvxc345ssdsds-508").withJson(targetStream));
        client.index(request);
        System.out.println("=====");
    }

    public static void addDoc2() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(new PlanObject(
                "example.com", "12xvxc345ssdsds-5088", "plan", "inNetwork", "12-12-2017", "plan"));
        InputStream targetStream = new ByteArrayInputStream(jsonPayload.getBytes());
        IndexRequest<Object> request = IndexRequest.of(b -> b.index("plan").id("plan__12xvxc345ssdsds-5088").withJson(targetStream));
        client.index(request);
        System.out.println("=====");
    }

    public static void deleteDoc() throws IOException {
        DeleteRequest deleteRequest = DeleteRequest.of(b -> b.index("plan").id("plan___12xvxc345ssdsds-508"));
        client.delete(deleteRequest);
    }

    public static void deleteDoc2() throws IOException {
        DeleteRequest deleteRequest = DeleteRequest.of(b -> b.index("products").id("abcd"));
        client.delete(deleteRequest);
    }

    public static void deleteDoc(ESRequest esRequest) throws IOException {
        DeleteRequest deleteRequest = DeleteRequest.of(b -> b.index(esRequest.getIndexName()).id(esRequest.getId()));
        client.delete(deleteRequest);
    }

    public static void addDoc(ESRequest esRequest) throws IOException {
        InputStream targetStream = new ByteArrayInputStream(esRequest.getRequestBody().getBytes());
        IndexRequest<Object> request = IndexRequest.of(b -> {
            IndexRequest.Builder<Object> builder = b.index(esRequest.getIndexName()).id(esRequest.getId()).withJson(targetStream);
            if (esRequest.isRouting()) {
                builder.routing("1");
            }
            return builder;
        });
        client.index(request);
        System.out.println("******");
        System.out.println(esRequest);
    }

    public static void processRequest(ESRequest esRequest) throws IOException {
        if (esRequest.isToDelete()) {
            deleteDoc(esRequest);
        } else {
            addDoc(esRequest);
        }
    }

    public static void main(String[] args) throws IOException {
        deleteDoc();
    }



    public static void processProduct(Object product) {
        System.out.println("----");
        System.out.println(product);
    }
}

class PlanObject {
    public String _org;
    public String objectId;
    public String objectType;
    public String planType;
    public String creationDate;
    public String plan_join_field;

    public PlanObject(String _org, String objectId, String objectType, String planType, String creationDate, String plan_join_field) {
        this._org = _org;
        this.objectId = objectId;
        this.objectType = objectType;
        this.planType = planType;
        this.creationDate = creationDate;
        this.plan_join_field = plan_join_field;
    }
}

class Product implements Serializable {
    public String name;
    public long price;

    public Product(String name, long price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
