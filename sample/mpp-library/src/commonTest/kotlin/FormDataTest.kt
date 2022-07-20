/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import cases.formData.apis.AuthApi
import cases.formData.models.Response
import cases.formData.models.SignupRequest
import dev.icerock.moko.network.multipart.MultiPartContent
import io.ktor.client.engine.mock.*
import io.ktor.client.request.forms.*
import io.ktor.http.ContentType
import io.ktor.http.content.MultiPartData
import io.ktor.util.AttributeKey
import io.ktor.util.toCharArray
import io.ktor.utils.io.bits.Memory
import io.ktor.utils.io.bits.storeByteArray
import io.ktor.utils.io.core.AbstractInput
import io.ktor.utils.io.core.ExperimentalIoApi
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.internal.ChunkBuffer
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okio.Buffer
import okio.Source
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormDataTest {
    @Test
    fun `formData body`() {
        val data: ByteArray = mockData()
        val source: Source = data.toSource()
        val input: Input = source.asInput()

        val api = createApi { request ->
            val body = request.body
            assertTrue(body is MultiPartFormDataContent)
            // TODO #122 fix test and logic of generated formdata support
            assertContains(body.toByteArray().decodeToString(), "{\"firstName\":\"first\",\"lastName\":\"last\",\"phone\":\"+799\",\"email\":\"a@b\",\"password\":\"111\",\"passwordRepeat\":\"111\",\"countryId\":1,\"cityId\":2,\"company\":\"test\"}", true)
            assertContains(body.toByteArray().decodeToString(), data.decodeToString())

            respondOk(
                """
{
    "status": 200,
    "message": "ok",
    "timestamp": 1001.0,
    "success": true
}
            """.trimIndent()
            )
        }

        val result = runBlocking {
            api.signup(
                signup = SignupRequest(
                    firstName = "first",
                    lastName = "last",
                    phone = "+799",
                    email = "a@b",
                    password = "111",
                    passwordRepeat = "111",
                    countryId = 1,
                    cityId = 2,
                    company = "test"
                ),
                avatar = input
            )
        }

        assertEquals(
            expected = Response(
                status = 200,
                message = "ok",
                timestamp = 1001.0,
                success = true
            ),
            actual = result
        )
    }

    private fun mockData(): ByteArray {
//        val random: Random = Random.Default
//        return ByteArray(64) { random.nextBytes(1)[0] }
        val testString: String = "Для женитьбы нужно было согласие отца, и для этого на другой день князь Андрей уехал к отцу.\n" +
                "Отец с наружным спокойствием, но внутренней злобой принял сообщение сына. Он не мог понять того, чтобы кто-нибудь хотел изменить жизнь, вносить в нее что-нибудь новое, когда жизнь для него уже кончалась. «Дали бы только дожить так, как я хочу, а потом бы делали, что хотели», — говорил себе старик. С сыном, однако, он употребил ту дипломацию, которую он употреблял в важных случаях. Приняв спокойный тон, он обсудил все дело:\n" +
                "Во-первых, женитьба была не блестящая в отношении родства, богатства и знатности. Во-вторых, князь Андрей был не первой молодости и слаб здоровьем (старик особенно налегал на это), а она была очень молода. В-третьих, был сын, которого жалко было отдать девчонке. В-четвертых, наконец, сказал отец, насмешливо глядя на сына, «я тебя прошу, отложи дело на год, съезди за границу, полечись, сыщи, как ты и хочешь, немца для князь Николая, и потом, ежели уж любовь, страсть, упрямство, что хочешь, так велики, тогда женись. И это последнее мое слово, знай, последнее...» — кончил князь таким тоном, которым показывал, что ничто не заставит его изменить свое решение.\n" +
                "Князь Андрей ясно видел, что старик надеялся, что чувство его или его будущей невесты не выдержит испытания года или что он сам, старый князь, умрет к этому времени, и решил исполнить волю отца: сделать предложение и отложить свадьбу на год.\n" +
                "Через три недели после своего последнего вечера у Ростовых князь Андрей вернулся в Петербург.\n" +
                "На другой день после своего объяснения с матерью Наташа ждала целый день Болконского, но он не приехал. На другой, на третий день было то же, Пьер также не приезжал, и Наташа, не зная того, что князь Андрей уехал к отцу, не могла объяснить его отсутствия.\n" +
                "Так прошли три недели. Наташа никуда не хотела выезжать, и, как тень, праздная и унылая, ходила по комнатам, вечером тайно от всех плакала и не являлась по вечерам к матери. Она беспрестанно краснела и раздражалась. Ей казалось, что все знают о ее разочаровании, смеются и жалеют о ней. При всей силе внутреннего горя, это тщеславное горе усиливало ее несчастие.\n" +
                "Однажды она пришла к графине, хотела что-то сказать ей и вдруг заплакала. Слезы ее были слезы обиженного ребенка, который сам не знает, за что он наказан. Графиня стала успокоивать Наташу. Наташа, вслушивавшаяся сначала в слова матери, вдруг прервала ее:\n" +
                "— Перестаньте, мама, я и не думаю и не хочу думать! Так, поездил, и перестал, и перестал...\n" +
                "Голос ее задрожал, она чуть не заплакала, но оправилась и спокойно продолжала:\n" +
                "— И совсем я не хочу выходить замуж. И я его боюсь; я теперь совсем, совсем успокоилась...\n" +
                "На другой день после этого разговора Наташа надела то старое платье, которое было ей особенно известно за доставляемую им по утрам веселость, и с утра начала тот свой прежний образ жизни, от которого она отстала после бала. Она, напившись чаю, пошла в залу, которую она особенно любила за сильный резонанс, и начала петь свои солфеджи (упражнения пения). Окончив первый урок, она остановилась на середине залы и повторила одну музыкальную фразу, особенно понравившуюся ей. Она прислушалась радостно к той (как будто неожиданной для нее) прелести, с которою эти звуки, переливаясь, наполнили всю пустоту залы и медленно замерли, и ей вдруг стало весело. «Что об этом думать много, и так хорошо», — сказала она себе и стала взад и вперед ходить по зале, ступая не простыми шагами по звонкому паркету, но на всяком шагу переступая с каблучка (на ней были новые, любимые башмаки) на носок, и так же радостно, как и к звукам своего голоса, прислушиваясь к этому мерному топоту каблучка и поскрипыванию носка. Проходя мимо зеркала, она заглянула в него. «Вот она я! — как будто говорило выражение ее лица при виде себя. — Ну, и хорошо. И никого мне не нужно».\n" +
                "Лакей хотел войти, чтобы убрать что-то в зале, но она не пустила его, опять, затворив за ним дверь, продолжала свою прогулку. Она возвратилась в это утро опять к своему любимому состоянию любви к себе и восхищения перед собою. «Что за прелесть эта Наташа! — сказала она опять про себя словами какого-то третьего, собирательного мужского лица. — Хороша, голос, молода, и никому она не мешает, оставьте только ее в покое». Но сколько бы ни оставляли ее в покое, она уже не могла быть покойна и тотчас же почувствовала это.\n" +
                "В передней отворилась дверь подъезда, кто-то спросил: дома ли? — и послышались чьи-то шаги. Наташа смотрелась в зеркало, но она не видала себя. Она слушала звуки в передней. Когда она увидала себя, лицо ее было бледно. Это был он. Она это верно знала, хотя чуть слышала звук его голоса из затворенных дверей.\n" +
                "Наташа, бледная и испуганная, вбежала в гостиную.\n" +
                "— Мама, Болконский приехал! — сказала она. — Мама, это ужасно, это несносно! Я не хочу... мучиться! Что же мне делать?..\n" +
                "Еще графиня не успела ответить ей, как князь Андрей с тревожным и серьезным лицом вошел в гостиную. Как только он увидал Наташу, лицо его просияло. Он поцеловал руку графини и Наташи и сел подле дивана...\n" +
                "— Давно уже мы не имели удовольствия... — начала было графиня, но князь Андрей перебил ее, отвечая на ее вопрос и, очевидно, торопясь сказать то, что ему было нужно.\n" +
                "— Я не был у вас все это время, потому что был у отца: мне нужно было переговорить с ним о весьма важном деле. Я вчера ночью только вернулся, — сказал он, взглянув на Наташу. — Мне нужно переговорить с вами, графиня, — прибавил он после минутного молчания.\n" +
                "Графиня, тяжело вздохнув, опустила глаза.\n"
        return testString.toByteArray()
    }

    private fun ByteArray.toSource(): Source = Buffer().also { it.write(this) }

    private fun createApi(mock: MockRequestHandler): AuthApi {
        val json = Json.Default
        val httpClient = createMockClient(json, mock)
        return AuthApi(
            basePath = "https://localhost",
            httpClient = httpClient,
            json = json
        )
    }
}

internal class SourceAsInput(
    private val source: Source
) : @Suppress("DEPRECATION") AbstractInput(pool = ChunkBuffer.Pool) {

    @OptIn(ExperimentalIoApi::class)
    override fun fill(destination: Memory, offset: Int, length: Int): Int {
        println("fill $offset $length")
        val buffer = Buffer()
        source.read(buffer, length.toLong())

        val chunkByteArray: ByteArray = buffer.readByteArray()
        destination.storeByteArray(offset, chunkByteArray)

        return chunkByteArray.size
    }

    override fun closeSource() {
        source.close()
    }
}

fun Source.asInput(): Input = SourceAsInput(this)
