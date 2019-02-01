package plp.project02.util;

import java.util.ArrayList;
import java.util.List;

import plp.project02.engine.Object;

public class InstancePool<T> implements Object {
	public interface PoolObjectFactory<T> {
		public T createObject();
	}
	
	private final List<T> freeObjects;
	private final PoolObjectFactory<T> factory;
	private final int maxSize;
	
	public InstancePool(PoolObjectFactory<T> factory, int maxSize) {
		this.factory = factory;
		this.maxSize = maxSize;
		this.freeObjects = new ArrayList<T>(maxSize);
	}
	
	public T newObject() {
		T object = null;
		
		if (freeObjects.size() == 0) {
			object = factory.createObject();
		}
		else {
			object = freeObjects.remove(freeObjects.size()-1);
		}
		
		return object;
	}
	
	public void free(T object) {
		if (freeObjects.size() < maxSize) {
			freeObjects.add(object);
		}
	}
	
	public boolean release() {
		freeObjects.clear();
		return true;
	}

}
