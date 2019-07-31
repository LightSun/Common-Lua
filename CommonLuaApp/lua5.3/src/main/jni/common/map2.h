//
// Created by Administrator on 2019/7/31.
//

#ifndef COMMONLUAAPP_MAP2_H
#define COMMONLUAAPP_MAP2_H

#include "map"


template <typename K, typename V>
class Map{
private:
    std::map<K, V> _map;

public:
    typedef bool (*Traveller)(const Map<K, V>* map,const K& key,const V& value);

    Map(const std::map<K, V>& map){
        this->_map = map;
    }
    Map(){}

    const V get(K& key){
        auto it = _map.find(key);
        return it->second;
    }
    const V remove(K& key){
        auto it = _map.find(key);
        if(_map.erase(key)){
            return it->second;
        }
        return nullptr;
    }
    const V put(K& key, V& val){
        V old = _map[key];
        _map[key] = val;
        return old;
    }
    const bool containsKey(K& key){
        return get(key) != nullptr;
    }
    const K ofKey(V& val){
        K* ptr = nullptr;
        auto tr = [val](const Map<K, V>* map,const K& key,const V& value){
            if(value == val){
                ptr = &key;
                return true;
            }
            return false;
        };
        travel(tr);
        return ptr != nullptr ? *ptr : nullptr;
    }
    const bool containsValue(V& val){
        return ofKey(val) != nullptr;
    }
    const int size(){
        return this->_map.size();
    }
    // true if breaked,  false otherwise.
    const bool travel(Traveller& t){
        assert(t != nullptr);
        for (auto &it = _map.begin(); it != _map.end() ; ++it) {
            if(t(this, it->first, it->second)){
                return true;
            }
        }
        return false;
    }
    Map<K,V> &operator=(const Map<K,V> &array1){
        if(&array1 != this){
            this->_map = array1._map;
        }
        return *this;
    }
    V& operator[](const K& __k){
        return _map[__k];
    }
};

#endif //COMMONLUAAPP_MAP2_H