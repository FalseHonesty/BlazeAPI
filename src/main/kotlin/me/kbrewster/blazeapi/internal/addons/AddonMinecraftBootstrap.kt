package me.kbrewster.blazeapi.internal.addons

import com.github.kevinpriv.GuiTest
import me.kbrewster.blazeapi.EVENTBUS
import me.kbrewster.blazeapi.LOADED_ADDONS
import me.kbrewster.blazeapi.internal.addons.misc.AddonLoadException
import java.util.*

/**
 * Instance created on the classloader {@link net.minecraft.launchwrapper.LaunchClassLoader}
 *
 * @since 1.0
 * @author Kevin Brewster
 */
object AddonMinecraftBootstrap {


    /**
     * The <i>init</i> phase of the bootstrap where the
     * instances are created and loaded to {@link me.kbrewster.blazeapi.BlazeAPI#LOADED_ADDONS}
     * and then sets the phase to {@link Phase#DEFAULT}.
     *
     * This should be called when <i>Minecraft</i> is starting
     * In this case we use the start of {@link net.minecraft.client.Minecraft#init}
     */
    @JvmStatic
    fun init() {
        if (AddonBootstrap.phase != AddonBootstrap.Phase.INIT) {
            throw AddonLoadException("Bootstrap is currently at Phase.${AddonBootstrap.phase} when it should be at Phase.INIT")
        }

        val loaded = AddonBootstrap.addonManifests
                .map { Class.forName(it.mainClass).newInstance() }
                .filter { it is Addon }
                .map { it as Addon }
                .toCollection(ArrayList())

        LOADED_ADDONS.addAll(loaded)
        LOADED_ADDONS.forEach(Addon::onEnable)
        AddonBootstrap.phase = AddonBootstrap.Phase.DEFAULT
        EVENTBUS.register(GuiTest())
    }

}