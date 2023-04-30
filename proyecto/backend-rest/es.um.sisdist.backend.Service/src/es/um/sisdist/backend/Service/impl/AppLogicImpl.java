/**
 *
 */
package es.um.sisdist.backend.Service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import es.um.sisdist.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.backend.grpc.PingRequest;
import es.um.sisdist.models.D;
import es.um.sisdist.models.DBDTO;
import es.um.sisdist.models.DBDTOUtils;
import es.um.sisdist.models.UserDTO;
import es.um.sisdist.models.UserDTOUtils;
import es.um.sisdist.backend.dao.DAOFactoryImpl;
import es.um.sisdist.backend.dao.IDAOFactory;
import es.um.sisdist.backend.dao.models.DB;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.backend.dao.models.utils.UserUtils;
import es.um.sisdist.backend.dao.user.IUserDAO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * @author dsevilla
 * 
 */
public class AppLogicImpl
{
    IDAOFactory daoFactory;
    IUserDAO dao;

    private static final Logger logger = Logger.getLogger(AppLogicImpl.class.getName());

    private final ManagedChannel channel;
    private final GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub;
    //private final GrpcServiceGrpc.GrpcServiceStub asyncStub;

    static AppLogicImpl instance = new AppLogicImpl();

    private AppLogicImpl()
    {
        daoFactory = new DAOFactoryImpl();
        Optional<String> backend = Optional.ofNullable(System.getenv("DB_BACKEND"));
        
        if (backend.isPresent() && backend.get().equals("mongo"))
            dao = daoFactory.createMongoUserDAO();
        else
            dao = daoFactory.createSQLUserDAO();

        var grpcServerName = Optional.ofNullable(System.getenv("GRPC_SERVER"));
        var grpcServerPort = Optional.ofNullable(System.getenv("GRPC_SERVER_PORT"));

        channel = ManagedChannelBuilder
                .forAddress(grpcServerName.orElse("localhost"), Integer.parseInt(grpcServerPort.orElse("50051")))
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid needing certificates.
                .usePlaintext().build();
        blockingStub = GrpcServiceGrpc.newBlockingStub(channel);

    }

    public static AppLogicImpl getInstance()
    {
        return instance;
    }

    public Optional<User> getUserByEmail(String userId)
    {
        Optional<User> u = dao.getUserByEmail(userId);
        return u;
    }

    public Optional<User> getUserById(String userId)
    {
        return dao.getUserById(userId);
    }

    public boolean ping(int v)
    {
    	logger.info("Issuing ping, value: " + v);
    	
        // Test de grpc, puede hacerse con la BD
    	var msg = PingRequest.newBuilder().setV(v).build();
        var response = blockingStub.ping(msg);
        
        return response.getV() == v;
    }

    // El frontend, a través del formulario de login,
    // envía el usuario y pass, que se convierte a un DTO. De ahí
    // obtenemos la consulta a la base de datos, que nos retornará,
    // si procede,
    public Optional<User> checkLogin(String email, String pass)
    {
        Optional<User> u = dao.getUserByEmail(email);

        if (u.isPresent())
        {
            String hashed_pass = UserUtils.md5pass(pass);
            if (0 == hashed_pass.compareTo(u.get().getPassword_hash()))
                return u;
        }

        return Optional.empty();
    }
    
    public User register(UserDTO userDTO) {

        User registered = new User();
        registered.setDbs(new ArrayList<>());
        registered.setEmail(userDTO.getEmail());
        registered.setName(userDTO.getName());
        registered.setPassword_hash(UserUtils.md5pass(userDTO.getPassword()));
        registered.setToken("token");
        registered.setVisits(0);
        registered.setUid(UserUtils.md5pass(registered.getEmail()));

        dao.addUsr(registered);
        return registered;
    }
    

	public Optional<DB> addDB(String email, DBDTO dbdto) {
		User dbOwner = getUserByEmail(email).get();
        DB db = DBDTOUtils.fromDTO(dbdto);
        System.out.println(db.toString());
		dbOwner.addDB(db);
		dao.updateUsr(dbOwner);
		return Optional.of(db);
	}

    public Optional<DB> getDB(String email, String dbName) {
        Optional<User> dbOwner = getUserByEmail(email);
        if  (dbOwner.isEmpty()) {
            System.out.println("No existe el usuario");
            return Optional.empty();
        }
        
        Optional<DB> db = dbOwner.get().getDB(dbName);
        System.out.println(db.toString());
        return db;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = dao.getAllUsers();
        return users.stream().map(UserDTOUtils::toDTO).collect(Collectors.toList());
    }

    public boolean deleteDB(String email, String dbName) {
        Optional<User> dbOwner = getUserByEmail(email);
        if  (dbOwner.isEmpty()) {
            System.out.println("No existe el usuario");
            return false;
        }
        
        boolean isDeleted= dbOwner.get().deleteDB(dbName);
        dao.updateUsr(dbOwner.get());
        return isDeleted;
    }

    public boolean updateDB(String email, String dbname,  DBDTO dbdto) {
        Optional<User> dbOwner = getUserByEmail(email);
        if  (dbOwner.isEmpty()) {
            System.out.println("No existe el usuario");
            return false;
        }
        
        boolean isUpdated= dbOwner.get().updateDB(dbname, DBDTOUtils.fromDTO(dbdto));
        dao.updateUsr(dbOwner.get());
        return isUpdated;
    }

    
    
}
