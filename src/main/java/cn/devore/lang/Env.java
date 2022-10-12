package cn.devore.lang;

import cn.devore.Devore;
import cn.devore.module.Module;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Env {
    private final Map<String, Token> _env;
    private final Env _father;
    private final Lock lock = new ReentrantLock();

    public Env() {
        this(null);
    }

    public Env(Env father) {
        this(new HashMap<>(), father);
    }

    public Env(Map<String, Token> env, Env father) {
        this._env = env;
        this._father = father;
    }

    public Env load(String path) {
        return load(Devore._module.get(path));
    }

    public Env load(Module module) {
        module.init(this);
        return this;
    }

    public void remove(String key) {
        Env data = this;
        while (data._father != null && !data._env.containsKey(key))
            data = data._father;
        lock.lock();
        try {
            data._env.remove(key);
        } finally {
            lock.unlock();
        }
    }

    public void set(String key, Token value) {
        Env data = this;
        while (data._father != null && !data._env.containsKey(key))
            data = data._father;
        lock.lock();
        try {
            data.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    public void put(String key, Token value) {
        _env.put(key, value);
    }

    public boolean contains(String key) {
        Env data = this;
        while (data._father != null && !data._env.containsKey(key))
            data = data._father;
        return data._env.containsKey(key);
    }

    public Token get(String key) {
        var data = this;
        while (data._father != null && !data._env.containsKey(key))
            data = data._father;
        return data._env.get(key);
    }

    public Env createChild() {
        return new Env(this);
    }
}
