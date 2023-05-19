package impl;

import java.util.List;
import java.util.Optional;

import es.um.sisdist.backend.dao.DAOFactoryImpl;
import es.um.sisdist.backend.dao.IDAOFactory;
import es.um.sisdist.backend.dao.models.*;
import es.um.sisdist.backend.dao.models.utils.UserUtils;
import es.um.sisdist.backend.dao.user.IUserDAO;
import es.um.sisdist.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.backend.grpc.RPCMapReduceRequest;
import es.um.sisdist.models.D;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class ExternalServiceImpl {
    
    private static ExternalServiceImpl instance;

    private IDAOFactory daoFactory;
    private IUserDAO userDAO;
    private final ManagedChannel channel;
    private final GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub;
    private final GrpcServiceGrpc.GrpcServiceStub asyncStub;
    
    private ExternalServiceImpl() {
        daoFactory = new DAOFactoryImpl();
        userDAO = daoFactory.createMongoUserDAO();
        
        var grpcServerName = Optional.ofNullable(System.getenv("GRPC_SERVER"));
        var grpcServerPort = Optional.ofNullable(System.getenv("GRPC_SERVER_PORT"));
        
        
        channel = ManagedChannelBuilder
                .forAddress(grpcServerName.orElse("localhost"), Integer.parseInt(grpcServerPort.orElse("50051")))
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid needing certificates.
                .usePlaintext().build();
        blockingStub = GrpcServiceGrpc.newBlockingStub(channel);
        asyncStub = GrpcServiceGrpc.newStub(channel);
    }
    
    public Optional<User> getUserByEmail(String userId)
    {
        Optional<User> u = userDAO.getUserByEmail(userId);
        return u;
    }

    public Optional<User> getUserById(String userId)
    {
        return userDAO.getUserById(userId);
    }


    public static ExternalServiceImpl getInstance() {
        if (instance == null) {
            instance = new ExternalServiceImpl();
        }
        return instance;
    }

    public Optional<User> getUser(String email) {
        return userDAO.getUserByEmail(email);
    }

    public boolean checkAuthToken(String authToken, User u, String date, String url){

        //AuthToken = md5(url + date + token)
        System.out.println("REQUEST AUTH-TOKEN: " + authToken);
        String userAuthToken = UserUtils.md5pass(url + date + u.getToken());
        System.out.println("MD5 WITH...: " + url + date + u.getToken());
        System.out.println("CALCULATED AUTH-TOKEN: " + userAuthToken);
        return userAuthToken.equals(authToken);
    }

    public void addMapReduce(String email, String dbname, String map, String reduce, String outDB, String mrID) {
        Optional<User> dbOwner = getUserByEmail(email);
        if  (dbOwner.isEmpty()) {
            System.out.println("No existe el usuario");
            return;
        }
        //dbOwner.get().mapReduce(dbname, map, reduce);
        //dao.updateUsr(dbOwner.get());
        // Test de grpc, puede hacerse con la BD
    	var msg = RPCMapReduceRequest.newBuilder()
        .setMap(map)
        .setReduce(reduce)
        .setOutDb(outDB)
        .setUser(email)
        .setInDb(dbname)
        .build( );
        asyncStub.mapReduce(msg, new StreamObserver<RPCMapReduceRequest>() {
            @Override
            public void onNext(RPCMapReduceRequest value) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
            
        });
        //var response = blockingStub.mapReduce(msg);
        return;
    }
    
    public List<Pair> queryDB(String email, String dbname, String query) {
        Optional<User> dbOwner = getUserByEmail(email);
        if  (dbOwner.isEmpty()) {
            System.out.println("No existe el usuario");
            return null;
        }
        List<Pair> pairs = dbOwner.get().queryDB(dbname, query);
        return pairs;
    }
    
    public boolean addKeyValue(String email, String dbname, String key, String value) {
        Optional<User> dbOwner = getUserByEmail(email);
        if  (dbOwner.isEmpty()) {
            System.out.println("No existe el usuario");
            return false;
        }
        
        boolean isAdded = dbOwner.get().addPair(dbname, key, value);
        userDAO.updateUsr(dbOwner.get());
        return isAdded;
    }
    
    public D getKeyValue(String email, String dbname, String key) {
        Optional<User> dbOwner = getUserByEmail(email);
        if  (dbOwner.isEmpty()) {
            System.out.println("No existe el usuario");
            return null;
        }
        
        Pair p = dbOwner.get().getPair(dbname, key).get(); 
        return new D(p.getKey(), p.getValue());

    }

    public boolean updateKeyValue(String email, String dbname, String key, String value) {
        Optional<User> dbOwner = getUserByEmail(email);
        if  (dbOwner.isEmpty()) {
            System.out.println("No existe el usuario");
            return false;
        }
        
        boolean isAdded = dbOwner.get().updatePair(dbname, key, value);
        userDAO.updateUsr(dbOwner.get());
        return isAdded;
    }

    public boolean deleteKeyValue(String email, String dbname, String key) {
        Optional<User> dbOwner = getUserByEmail(email);
        if  (dbOwner.isEmpty()) {
            System.out.println("No existe el usuario");
            return false;
        }
        
        boolean isDeleted = dbOwner.get().deletePair(dbname, key);
        userDAO.updateUsr(dbOwner.get());
        return isDeleted;
    }


}