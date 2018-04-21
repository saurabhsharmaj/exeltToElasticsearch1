
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

public class IngestService {

    Client client;

    public IngestService(Client client) {
        this.client = client;
    }

    public boolean ingest(String bucket, String type, Map<String, String> dataMap) {
      	ActionFuture<IndexResponse> response = client.prepareIndex(bucket,type)
    	        .setSource(dataMap).execute();
         return true;
    }

	public boolean ingest(String type, List<String> docs) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        docs.forEach(doc -> bulkRequest.add(client.prepareIndex("git", type).setSource(doc)));
        return bulkRequest.get().hasFailures();

    }


}