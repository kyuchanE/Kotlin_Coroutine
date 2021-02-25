package com.example.coroutine_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.MainThread
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 *  21.02.24
 *  chan9U
 *  Coroutine (Light-Weighted-Thread)
 */
class MainActivity : AppCompatActivity(), CoroutineScope {
    /**
     * GlobalScope를 사용하였는데
     * 이 스코프는 Application이 종료될 때 까지 코루틴을 실행시킬 수 있습니다.
     * 만약 Activity에서 코루틴을 GlobalScope영역에서 실행시켰다면,
     * Activity가 종료되도 코루틴은 작업이 끝날 때까지 동작합니다.
     *
     * Activity가 종료되었다면 그 작업은 불필요한 리소스를 낭비하고 있는 것입니다.
     * Activity가 종료될 때 실행중인 코루틴도 함께 종료되길 원한다면 Activity의 Lifecycle과 일치하는 Scope에 코루틴을 실행시키면 됩니다.
     */
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()
        Log.d("@@@@ Main " , " onCreate >> ")

        /**
         * Dispatchers.Main : 안드로이드의 메인 쓰레드입니다. UI 작업은 여기서 처리되어야 합니다.
         * Dispatchers.IO : Disk 또는 네트워크에서 데이터 읽는 I/O 작업은 이 쓰레드에서 처리되어야 합니다. 예를들어, 파일을 읽거나 AAC의 Room 등도 여기에 해당됩니다.
         * Dispatchers.Default : 그외 CPU에서 처리하는 대부분의 작업들은 이 쓰레드에서 처리하면 됩니다.
         *
         *
         * CoroutineContext 와 CoroutineScope란 무엇인가?
         * CoroutineContext 4가지 메서드
         * get() : 연산자(operator) 함수로써 주어진 key 에 해당하는 컨텍스트 요소를 반환합니다.
         * fold() : 초기값(initialValue)을 시작으로 제공된 병합 함수를 이용하여 대상 컨텍스트 요소들을 병합한 후 결과를 반환합니다.
         * plus() : 현재 컨텍스트와 파라미터로 주어진 다른 컨텍스트가 갖는 요소들을 모두 포함하는 컨텍스트를 반환합니다.
         * minusKey() : 현재 컨텍스트에서 주어진 키를 갖는 요소들을 제외한 새로운 컨텍스트를 반환합니다.
         *
         * CoroutineContext 는 인터페이스로써 이를 구현한 구현체로는 다음과같은 3가지 종류
         * EmptyCoroutineContext: 특별히 컨텍스트가 명시되지 않을 경우 이 singleton 객체가 사용됩니다.
         * CombinedContext: 두개 이상의 컨텍스트가 명시되면 컨텍스트 간 연결을 위한 컨테이너역할을 하는 컨텍스트 입니다.
         * Element: 컨텍트스의 각 요소들도 CoroutineContext 를 구현합니다.
         *
         * ex)  launch() {}
         *      launch(Dispatchers.IO) {}
         *      launch(Dispatchers.IO + CoroutineName("ImageFetcher")) {}
         *      launch(Dispatchers.IO + CoroutineName("ImageFetcher") + CoroutineExceptionHandler()) {}
         *
         * CoroutineScope 는 기본적으로 CoroutineContext 하나만 멤버 속성으로 정의하고 있는 인터페이스 입니다.
         * 우리가 사용하는 모든 코루틴 빌더들(예> 코루틴 빌더- launch, async -, 스코프 빌더- coroutineScope, withContext - 등등)은
         * CoroutineScope 의 확장 함수로 정의 됩니다.
         * 다시말해, 이 빌더들은 CoroutineScope의 함수들인 것이고 이들이 코루틴을 생성할 때는 소속된 CoroutineScope 에 정의 된 CoroutineContext 를 기반으로 필요한 코루틴들을 생성해 내게 됩니다.
         */
        GlobalScope.launch(Dispatchers.Main) {
            Log.d("@@@@ Main " , " GlobalScope.launch(Dispatchers.Main) >> ")
            suspendFunction()

            val userOne = async(Dispatchers.IO) {
                suspendFunction()
            }
            val userTwo = launch(Dispatchers.Default) {
                ExSuspend.testSuspend()
            }

            /**
             * withContext() 라는 메소드도 있습니다.
             * 이것은 async와 동일한 역할을 하는 키워드입니다.
             * 차이점은 await()을 호출할 필요가 없다는 것입니다.
             * 결과가 리턴될 때까지 기다립니다.
             */
            val name = withContext(Dispatchers.Default + handler){

            }

            /**
             * launch, async 의 차이점??
             * async과 launch는 사실 동일한 컨셉입니다만 return하는 객체가 다릅니다.
             * launch -> Job return
             * async -> Deferred return
             * Job은 launch 시킨 코루틴의 상태를 관리하는 용도로 사용되는 반면 코루틴의 결과를 리턴받을 수 없으나
             * Deferred는 async 블럭에서 수행된 결과를 return 받을 수 있습니다.
             */
//            userOne.await()
//            userTwo.await()


            /**
             * scope.launch(Dispatchers.IO) {
             *      for (name in files) {
             *      yield() // or ensureActive()
             *      readFile(name)
             *      }
             * }
             *
             * 이렇게 리스트에 있는 모든 파일을 읽어오는 작업을 하는 도중에 scope.cancel()을 실행하면 어떻게 될까요?
             * 반복문의 실행을 멈출까요?
             *
             * 아쉽게도 그러지 않습니다.
             * 무거운 작업을 하는 코루틴을 취소(Cancelation) 하기 위해서는 협력(Co-operation)을 해야 합니다.
             *
             * 위 코드에서 코루틴은 파일들을 읽어오기 바빠서 Cancelation을 인지할 여유가 없습니다.
             * 따라서 무거운 작업을 할 때는 직접 Canceller를 작동하게 해줘야 합니다.
             *
             * ensureActive() 또는 yield()로 코루틴이 살아 있는지(cancel()이 호출되었는지) 확인할 수 있습니다.
             *
             * 스코프가 cancel 되고 ensureActive()나 yield()가 호출되면 실행 되고 있는 코루틴이 중지됩니다.
             */

        }

        /**
         * launch()의 인자에 플러스 연산자로 "handler"를 추가합니다.
         * 예외가 발생하면 이 handler로 콜백이 전달되어 예외처리를 할 수 있습니다.
         */
        launch(Dispatchers.Default + handler) {
            Log.d("@@@@ Main " , " launch(Dispatchers.Default) >> ")
            mainLaunch()
        }
    }

    /**
     * Suspending functions
     *
     */
    suspend fun suspendFunction() {
        var cnt = 0
        for (i in 0..100){
            delay(1000)
            Log.d("@@@@ suspendFunction" , ">> $cnt")
            cnt++
        }
    }

    suspend fun mainLaunch() {
        var cnt = 0
        for (i in 0..100){
            delay(1000)
            Log.d("@@@@ mainLaunch" , ">> $cnt")
            cnt++
        }
    }

    val handler = CoroutineExceptionHandler { coroutineScope, exception ->
        Log.d("@@@@@@", "$exception handled!")
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}