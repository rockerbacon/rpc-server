import com.lab309.middleware.RPCClient;

public class TestClient {

	private RPCClient rpcc;
	
	public TestClient () {
		this.rpcc = new RPCClient(5, 128000, 128000);
	}
	
}
