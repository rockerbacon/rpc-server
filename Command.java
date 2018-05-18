import com.lab309.network.UDPDatagram;

public class Command {
	private UDPDatagram dtgAssociated;
	
	public Command (UDPDatagram dtgAssociated) {
		this.dtgAssociated = dtgAssociated;
	}
	
	public UDPDatagram getDatagram () {
		return this.dtgAssociated;
	}
}
