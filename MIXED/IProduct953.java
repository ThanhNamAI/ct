package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IProduct953 extends Remote {
    public Product953 getProduct(String studentCode) throws RemoteException;
    public void order(Product953 product) throws RemoteException;
}
