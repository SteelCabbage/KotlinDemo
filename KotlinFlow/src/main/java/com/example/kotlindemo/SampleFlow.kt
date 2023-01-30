package com.example.demoflow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay

/**
 * @author liuyi@qingting.fm
 * @date 2023/01/29
 */

fun main() {
    runBlocking {
//        map()
//        filter()
//        onEach()
//        debounce()
//        sample()
//        reduce()
//        fold()
//        flatMapConcat()
//        flatMapMerge()
//        flatMapLatest()
//        zip()
//        buffer()
//        collectLatest()
        conflate()
    }
}

suspend fun conflate() {
    flow {
        var count = 0
        while (true) {
            emit(count)
            delay(1000)
            count++
        }
    }
        .conflate()
        .collect {
            println("start handle $it")
            delay(2000)
            println("finish handle $it")
        }
}

suspend fun collectLatest() {
    flow {
        var count = 0
        while (true) {
            emit(count)
            delay(1000)
            count++
        }
    }.collectLatest {
        println("start handle $it")
        delay(2000)
        println("finish handle $it")
    }
}

suspend fun buffer() {
    flow {
        emit(1)
        delay(1000)
        emit(2)
        delay(1000)
        emit(3)
    }.onEach {
        println("$it is ready")
    }.buffer()
        .collect {
            delay(1000)
            println("$it is handled")
        }
}

suspend fun zip() {
    val flow1 = flowOf("a", "b", "c")
    val flow2 = flowOf(1, 2, 3, 4, 5)
    flow1.zip(flow2) { f1, f2 ->
        f1 + f2
    }.collect {
        println(it)
    }
}

suspend fun flatMapLatest() {
    flow {
        emit(1)
        delay(150)
        emit(2)
        delay(50)
        emit(3)
    }.flatMapLatest {
        flow {
            delay(100)
            emit(it)
        }
    }
        .collect {
            println(it)
        }
}

suspend fun flatMapMerge() {
    flowOf(300, 200, 100)
        .flatMapMerge {
            flow {
                delay(it.toLong())
                emit("a$it")
                emit("b$it")
            }
        }
        .collect {
            println(it)
        }
}

suspend fun flatMapConcat() {
    flowOf(1, 2, 3)
        .flatMapConcat {
            flowOf("a$it", "b$it")
        }
        .collect {
            println(it)
        }
}

suspend fun fold() {
    val ret = flow {
        for (i in 'a'..'z')
            emit(i)
    }
        .fold("Alphabet:_") { acc, value -> acc + value }
    println(ret)
}

suspend fun reduce() {
    val ret = flow {
        for (i in 1..100) {
            emit(i)
        }
    }
        .reduce { accumulator, value -> accumulator + value }
    println(ret)
}

suspend fun sample() {
    flow {
        while (true) {
            emit("发送一条弹幕")
        }
    }
        .sample(1000)
        .flowOn(Dispatchers.IO)
        .collect {
            println(it)
        }
}

suspend fun debounce() {
    flow {
        emit(1)
        emit(2)
        delay(600)
        emit(3)
        delay(100)
        emit(4)
        delay(100)
        emit(5)
    }
        .debounce(500)
        .collect {
            println(it)
        }
}

suspend fun onEach() {
    val flow = flowOf(1, 2, 3, 4, 5)
    flow
        .filter {
            it % 2 == 0
        }
        .onEach {
            println("onEach_$it")
        }
        .map {
            it * it
        }
        .collect {
            println("collect_$it")
        }
}

suspend fun filter() {
    val flow = flowOf(1, 2, 3, 4, 5)
    flow
        .filter {
            it % 2 == 0
        }
        .map {
            it * it
        }
        .collect {
            println(it)
        }
}

suspend fun map() {
    val flow = flowOf(1, 2, 3, 4, 5)
    flow
        .map {
            it * it
        }
        .collect {
            println(it)
        }
}