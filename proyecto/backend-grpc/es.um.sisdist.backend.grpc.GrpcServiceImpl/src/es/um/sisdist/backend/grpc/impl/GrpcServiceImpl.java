package es.um.sisdist.backend.grpc.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import es.um.sisdist.backend.dao.DAOFactoryImpl;
import es.um.sisdist.backend.dao.IDAOFactory;
import es.um.sisdist.backend.dao.models.*;
import es.um.sisdist.backend.dao.user.IUserDAO;
import es.um.sisdist.backend.dao.user.MongoUserDAO;
import es.um.sisdist.backend.grpc.*;
import es.um.sisdist.backend.grpc.impl.jscheme.JSchemeProvider;
import es.um.sisdist.backend.grpc.impl.jscheme.MapReduceApply;
import io.grpc.stub.StreamObserver;
import jscheme.JScheme;
import jscheme.SchemePair;

class GrpcServiceImpl extends GrpcServiceGrpc.GrpcServiceImplBase 
{
	private Logger logger;
	private IDAOFactory daoFactory;
    private IUserDAO dao;

	//Create a lock
	private final ReentrantLock lock = new ReentrantLock();
	Map<String,Set<String>> inCourse;

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
		inCourse = new ConcurrentHashMap<>();
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
		String mrID = mrRequest.getId();
		
		lock.lock();
		Set<String> userInCourse = inCourse.get(mrRequest.getUser());
		if  (userInCourse == null) {
			userInCourse = new HashSet<String>();
			inCourse.put(mrRequest.getUser(), userInCourse);
		}
		userInCourse.add(mrID);
		lock.unlock();

		//Make other thread to do this async
		// Crear CompletableFuture para ejecutar el proceso de mapReduce de forma asíncrona
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			// Código del mapReduce
			// ...		
			logger.info("Iniciando js...");
			//Sleep 5 secs
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
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
			userO.get().addToMRHistory(mrID);
			dao.updateUsr(user);
			logger.info("Saved...");

		});

		// Agregar un callback para manejar la finalización del CompletableFuture
		future.thenRun(() -> {
			// Aquí puedes realizar cualquier acción después de que el mapReduce se haya completado
			lock.lock();
			inCourse.remove(mrID);
			// Comprobar si el proceso se ejecutó correctamente
			if (future.isCompletedExceptionally()) {
				// Ocurrió un error durante el mapReduce
				// Realizar acciones de manejo de errores...

			} else {
				// El mapReduce se ejecutó correctamente
				// Realizar acciones de éxito...
			}
			lock.unlock();
			responseObserver.onNext(RPCMapReduceRequest.newBuilder().setMap(mrRequest.getMap()).build());
			responseObserver.onCompleted();
		});


	}

	@Override
	public void getProcessingMR( GetProcessingMRRequest request, StreamObserver<GetProcessingMRResponse> responseObserver) 
	{
		logger.info("Recived PING request, value = " + request.getUser());
		lock.lock();
		Set<String> userInCourse = inCourse.get(request.getUser());
		lock.unlock();
		if  (userInCourse == null) {
			userInCourse = new HashSet<String>();
		}
		responseObserver.onNext(GetProcessingMRResponse.newBuilder().addAllProcessingIds(userInCourse).build());
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