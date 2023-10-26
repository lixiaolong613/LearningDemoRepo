package com.example.madslearning.utils

/**
 * @Author lilong
 * @Date 2023/10/20
 * @Description
 */
object DemoServiceLoader {

    fun <T> load(interfaceClass: Class<T>): T? {
        return load(interfaceClass, true)
    }

    fun <T> load(interfaceClass: Class<T>, needCatch : Boolean): T? {
        val it = loadIterator(interfaceClass, needCatch)
        return if (it.hasNext()) it.next() else null
    }

    fun <T> loadIterator(interfaceClass: Class<T>, preferFromCache: Boolean): Iterator<T> {
        return object: Iterator<T> {

            private var index = 0

            override fun hasNext(): Boolean {
                TODO("Not yet implemented")
            }

            override fun next(): T {
                TODO("Not yet implemented")
            }
        }
    }


}