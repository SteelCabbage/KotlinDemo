# Kotlin



[TOC]



## Flow

### 操作符

> https://mp.weixin.qq.com/s/fuUB-iYPjWaflCyPt5AQgQ



#### 0. setup

```kotlin
dependencies {
    ...
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1"
}
```



#### 1. map

```kotlin
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
```

> 1
>
> 4
>
> 9
>
> 16
>
> 25



#### 2. filter

```kotlin
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
```

> 4
>
> 16



#### 3. onEach

```kotlin
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
```

> onEach_2
>
> collect_4
>
> onEach_4
>
> collect_16



#### 4. debounce

```kotlin
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
```

> 2
>
> 5



#### 5. sample

```kotlin
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
```

> // 每间隔一秒，打印一行
>
> 发送一条弹幕
>
> 发送一条弹幕
>
> 发送一条弹幕
>
> ......



#### 6. reduce(终端操作符函数)

> 不需要借助collect函数，自己就能终结整个flow流程的操作符函数
>
> flow.reduce { acc, value -> acc + value }
>
> 其中acc是累积值，value则是当前值

```kotlin
suspend fun reduce() {
    val ret = flow {
        for (i in 1..100) {
            emit(i)
        }
    }
        .reduce { accumulator, value -> accumulator + value }
    println(ret)
}
```

> 5050



#### 7. fold(终端操作符函数, 需传入初始值)

> flow.fold(initial) { acc, value -> acc + value }

```kotlin
suspend fun fold() {
    val ret = flow {
        for (i in 'a'..'z')
            emit(i)
    }
        .fold("Alphabet:_") { acc, value -> acc + value }
    println(ret)
}
```

> Alphabet:_abcdefghijklmnopqrstuvwxyz



#### 8. flatMapConcat

> concat是连接的意思，数据是按照原有的顺序连接起来的

```kotlin
suspend fun flatMapConcat() {
    flowOf(1, 2, 3)
        .flatMapConcat {
            flowOf("a$it", "b$it")
        }
        .collect {
            println(it)
        }
}
```

> a1
>
> b1
>
> a2
>
> b2
>
> a3
>
> b3



#### 9. flatMapMerge

> merge是合并的意思，只保证将数据合并到一起，并不会保证顺序

```kotlin
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
```

> a100
>
> b100
>
> a200
>
> b200
>
> a300
>
> b300



#### 10. flatMapLatest

> collectLatest函数：它只接收处理最新的数据。如果有新数据到来了而前一个数据还没有处理完，则会将前一个数据剩余的处理逻辑全部取消
>
> 
>
> flatMapLatest函数类似，flow1中的数据传递到flow2中会立刻进行处理，但如果flow1中的下一个数据要发送了，而flow2中上一个数据还没处理完，则会直接将剩余逻辑取消掉，开始处理最新的数据

```kotlin
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
```

> 1
>
> 3



#### 11. zip

> zip连接的两个flow，它们之间是并行的运行关系
>
> 其中一个flow中的数据全部处理结束就会终止运行，剩余未处理的数据将不会得到处理
>
> 
>
> flatMap的运行方式是一个flow中的数据流向另外一个flow，是串行的关系

```kotlin
suspend fun zip() {
    val flow1 = flowOf("a", "b", "c")
    val flow2 = flowOf(1, 2, 3, 4, 5)
    flow1.zip(flow2) { f1, f2 ->
        f1 + f2
    }.collect {
        println(it)
    }
}
```

> a1
>
> b2
>
> c3



#### 12. buffer

> buffer， collectLatest，conflate：背压三剑客
>
> 所谓流速不均匀问题，指的就是Flow上游发送数据的速度和Flow下游处理数据的速度不匹配，从而可能引发的一系列问题
>
> 
>
> collect函数中的数据处理是会对flow函数中的数据发送产生影响的。
>
> 默认情况下，collect函数和flow函数会运行在同一个协程当中，因此collect函数中的代码没有执行完，flow函数中的代码也会被挂起等待。
>
> 
>
> buffer函数会让flow函数和collect函数运行在不同的协程当中，这样flow中的数据发送就不会受collect函数的影响了
>
> buffer函数其实就是一种背压的处理策略，它提供了一份缓存区，当Flow数据流速不均匀的时候，使用这份缓存区来保证程序运行效率。
>
> flow函数只管发送自己的数据，它不需要关心数据有没有被处理，反正都缓存在buffer当中。
>
> 而collect函数只需要一直从buffer中获取数据来进行处理就可以了
>

```kotlin
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
```

> 1 is ready
>
> 
>
> 2 is ready
>
> 1 is handled
>
> 
>
> 3 is ready
>
> 2 is handled
>
> 
>
> 3 is handled
>
> 
>
> // 若不加入buffer函数，则间隔一秒打一行，即每条数据处理时间是2秒



#### 13. conflate

> 流速不均匀问题持续放大，缓存区的内容越来越多时，引入新的策略conflate，适当地丢弃一些数据

```kotlin
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
```

> // 每间隔一秒，输出一行start日志，finish日志永远得不到输出，因为collectLatest函数的特性：
>
> // 当有新数据到来时而前一个数据还没有处理完，则会将前一个数据剩余的处理逻辑全部取消
>
> 
>
> conflate：
>
> 当前正在处理的数据无论如何都会处理完，准备去处理下一条数据时，直接处理最新的数据，中间的数据会被丢弃

```kotlin
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
```

> start handle 0
>
> 
>
> finish handle 0
>
> start handle 2
>
> 
>
> finish handle 2
>
> start handle 3
>
> 
>
> finish handle 3
>
> start handle 5
>
> // start日志和finish日志结对输出，有些数据则被完全丢弃掉，如1，4







