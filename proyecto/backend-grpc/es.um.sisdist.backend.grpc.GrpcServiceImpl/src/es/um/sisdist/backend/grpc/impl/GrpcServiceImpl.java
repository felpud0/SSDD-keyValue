package es.um.sisdist.backend.grpc.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import es.um.sisdist.backend.dao.DAOFactoryImpl;
import es.um.sisdist.backend.dao.IDAOFactory;
import es.um.sisdist.backend.dao.models.*;
import es.um.sisdist.backend.dao.user.IUserDAO;
import es.um.sisdist.backend.dao.user.MongoUserDAO;
import es.um.sisdist.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.backend.grpc.PingRequest;
import es.um.sisdist.backend.grpc.PingResponse;
import es.um.sisdist.backend.grpc.RPCMapReduceRequest;
import es.um.sisdist.backend.grpc.impl.jscheme.JSchemeProvider;
import es.um.sisdist.backend.grpc.impl.jscheme.MapReduceApply;
import io.grpc.stub.StreamObserver;
import jscheme.JScheme;
import jscheme.SchemePair;

class GrpcServiceImpl extends GrpcServiceGrpc.GrpcServiceImplBase 
{
	private Logger logger;
	IDAOFactory daoFactory;
    IUserDAO dao;
	
    public GrpcServiceImpl(Logger logger) 
    {
		super();
		this.logger = logger;
		daoFactory = new DAOFactoryImpl();
        Optional<String> backend = Optional.ofNullable(System.getenv("DB_BACKEND"));
        
        if (backend.isPresent() && backend.get().equals("mongo"))
            dao = daoFactory.createMongoUserDAO();
        else
            dao = daoFactory.createSQLUserDAO();
	}

	@Override
	public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) 
	{
		logger.info("Recived PING request, value = " + request.getV());
		responseObserver.onNext(PingResponse.newBuilder().setV(request.getV()).build());
		responseObserver.onCompleted();
	}

	@Override
	public void mapReduce(RPCMapReduceRequest mrRequest, StreamObserver<RPCMapReduceRequest> responseObserver) 
	{
		logger.info("Recived PING request, value = " + mrRequest.getMap());
		logger.info("Iniciando js...");
		JScheme jsc= JSchemeProvider.js();
		logger.info("Iniciando MRA...");
		// Mapper
		MapReduceApply mra = new MapReduceApply(jsc, mrRequest.getMap(), mrRequest.getReduce());		
		logger.info("Buscando...");
		Optional<User> userO =dao.getUserByEmail(mrRequest.getUser());
		if (!userO.isPresent())
		{
			logger.info("User not found: "+ mrRequest.getUser());
			responseObserver.onNext(RPCMapReduceRequest.newBuilder().setMap(mrRequest.getMap()).build());
			responseObserver.onCompleted();
			return;
		}
		User user = userO.get();
		Optional <DB> db = user.getDB(mrRequest.getInDb());
		if (!db.isPresent())
		{
			logger.info("DB not found: "+ mrRequest.getInDb());
			responseObserver.onCompleted();
			return;
		}

		List<Pair> values = db.get().getTables();

		List<SchemePair> schameValues = new ArrayList<>();


		//Aply values to with mra
		logger.info("A transformar...");

		values.stream().forEach(pair -> schameValues.add(JScheme.list(pair.getKey(),pair.getValue())));
		schameValues.stream().forEach(pair -> mra.apply(pair.first(), pair.second()));
		logger.info("A reducir...");
		Map<Object, Object> result =  mra.map_reduce();
		//Transform Map<Object, Object> to Map<String, String>
		logger.info("Result: "+ result.toString());

		List<Pair> pairResult = new ArrayList<>();
		for (Object key : result.keySet())
			pairResult.add(new Pair(key.toString(), result.get(key).toString()));
		
		//Save result in DB
		logger.info("Saving...");
		user.addDB(new DB(mrRequest.getOutDb(), pairResult));
		dao.updateUsr(user);

		responseObserver.onNext(RPCMapReduceRequest.newBuilder().setMap(mrRequest.getMap()).build());
		responseObserver.onCompleted();
	}


/*
	@Override
	public void storeImage(ImageData request, StreamObserver<Empty> responseObserver)
    {
		logger.info("Add image " + request.getId());
    	imageMap.put(request.getId(),request);
    	responseObserver.onNext(Empty.newBuilder().build());
    	responseObserver.onCompleted();
	}

	@Override
	public StreamObserver<ImageData> storeImages(StreamObserver<Empty> responseObserver) 
	{
		// La respuesta, sólo un objeto Empty
		responseObserver.onNext(Empty.newBuilder().build());

		// Se retorna un objeto que, al ser llamado en onNext() con cada
		// elemento enviado por el cliente, reacciona correctamente
		return new StreamObserver<ImageData>() {
			@Override
			public void onCompleted() {
				// Terminar la respuesta.
				responseObserver.onCompleted();
			}
			@Override
			public void onError(Throwable arg0) {
			}
			@Override
			public void onNext(ImageData imagedata) 
			{
				logger.info("Add image (multiple) " + imagedata.getId());
		    	imageMap.put(imagedata.getId(), imagedata);	
			}
		};
	}

	@Override
	public void obtainImage(ImageSpec request, StreamObserver<ImageData> responseObserver) {
		// TODO Auto-generated method stub
		super.obtainImage(request, responseObserver);
	}

	@Override
	public StreamObserver<ImageSpec> obtainCollage(StreamObserver<ImageData> responseObserver) {
		// TODO Auto-generated method stub
		return super.obtainCollage(responseObserver);
	}
	*/
}