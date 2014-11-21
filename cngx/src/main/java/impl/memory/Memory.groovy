package impl.memory

import api.memory.IMemory
import api.memory.ISession

class Memory extends IMemory {

    @Override
    ISession GetSession(String username, String password, boolean writeable, boolean exclusive) {
        return null
    }
}
