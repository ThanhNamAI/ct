package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IProduct954 extends Remote {
    public Product954 getProduct(String studentCode) throws RemoteException;
    public void order(Product954 product) throws RemoteException;
}
