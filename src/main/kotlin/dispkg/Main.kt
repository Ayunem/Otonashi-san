package dispkg

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import net.ayataka.kordis.DiscordClient
import net.ayataka.kordis.Kordis
import net.ayataka.kordis.Kordis.create
import net.ayataka.kordis.event.EventHandler
import net.ayataka.kordis.event.events.message.MessageReceiveEvent
import org.jsoup.Jsoup

fun main() = runBlocking{
    Main().run()
}

class Main {
    private lateinit var client: DiscordClient
    suspend fun run() {
        client = create {
            //Discord BOT Token
            this.token = "TOKEN"
            addListener(this@Main)
        }
    }

    @EventHandler
    suspend fun onMessageReceived(event: MessageReceiveEvent) {
        val member = event.message.member ?: return
        val channel = event.message.serverChannel ?: return
        val message = event.message

        //Bot is over
        if (member.bot) return

        //Discordの仕組み上、空白を送れないので拡張性の意味のif
        if(message.content.isNotEmpty()) {
            val idol_name = event.message.content
            val imas_url = "http://api.imas-db.jp/character/lookup?include_profile=true&name=$idol_name"
            val json = Jsoup.connect(imas_url).ignoreContentType(true).execute().body()

            val mapper = jacksonObjectMapper()
            val idol = try { mapper.readValue<dataList>(json) } catch (e: MissingKotlinParameterException) {
                event.message.channel.send("入力に間違いがあります")
                return
            }

            val chr = idol.character_list[0]

            event.message.channel.send{
                embed {
                    author(name = "$idol_name (${chr.name_ruby})")
                    field("Birthday", "${chr.birth_month}月${chr.birth_day}日", true)
                    field("Arrivaldate", chr.arrival_date, true)
                    field("CV", chr.cv, true)

                    for (pro in chr.profile_list) {
                        val hand = when(pro!!.dominant_hand) {
                            "R"     -> "右利き"
                            "L"     -> "左利き"
                            else    -> "未設定"
                        }
                        //fieldのValueは""だとメッセージが破棄されるので未設定を返して動作させる: "未設定"etc
                        val favorite = when(pro.favorite) {
                            ""      -> "未設定"
                            else    -> pro.favorite
                        }
                        val specialty = when(pro.specialty) {
                            ""      -> "未設定"
                            else    -> pro.specialty
                        }
                        val hometown = when(pro.hometown) {
                            ""      -> "未設定"
                            else    -> pro.hometown
                        }
                        val gen = when(pro.generation) {
                            1       -> " 1st.Generation "
                            2       -> " 2nd.Generation "
                            else    -> " elseはなさそう "
                        }

                        field("[$gen]", "-------------------------------------------------------------------------------------")
                        field("Profile", "${pro.age}歳 $hand (${pro.blood_type}型)", true)
                        field("Height&Weight", "${pro.height}cm / ${pro.weight}kg", true)
                        field("3size", "B${pro.bust}/W${pro.waist}/H${pro.hip}", true)
                        field("Hobby", "$pro.hobby", true)
                        field("Favorite", favorite, true)
                        field("Specialty", specialty, true)
                        //346アイドルは出身地がアイマスDBに設定されているため表示する
                        if (chr.origin_media.contains("シンデレラガールズ")) field("Hometown", hometown)
                    }
                }
            }
        }
    }
}