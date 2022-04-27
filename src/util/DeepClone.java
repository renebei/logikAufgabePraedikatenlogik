package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
basiert auf https://stackoverflow.com/questions/2156120/java-recommended-solution-for-deep-cloning-copying-an-instance (am Ende)
 Jede serialisierbare Klasse K, die das Interface DeepClone<K> in seiner
 implements-Liste stehen hat, bekommt so eine Methode deepClone() zur Erstellung
 tiefer Kopien.
 */

public interface DeepClone<T extends Serializable> extends Serializable{

	@SuppressWarnings("unchecked")
	default public T deepClone() {
	  ByteArrayOutputStream bos = null;
	  ByteArrayInputStream bis = null;
		try {
			bos = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(bos);
			o.writeObject(this);		
			bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream oi = new ObjectInputStream(bis);
			Object ob = oi.readObject();
			return (T) ob;
		} catch (IOException | ClassNotFoundException e) {
			throw new IllegalArgumentException("clone() nicht moeglich");
		} finally {
      try {
        if (bis != null) {
          bis.close();
        }
        if (bos != null) {
          bos.close();
        }
      } catch (IOException e) {
      }
    }
	}
}
