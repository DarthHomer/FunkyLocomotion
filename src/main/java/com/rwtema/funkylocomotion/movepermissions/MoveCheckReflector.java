package com.rwtema.funkylocomotion.movepermissions;

import com.mojang.authlib.GameProfile;
import com.rwtema.funkylocomotion.api.FunkyCapabilities;
import com.rwtema.funkylocomotion.api.IMoveCheck;
import com.rwtema.funkylocomotion.proxydelegates.ProxyRegistry;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class MoveCheckReflector implements IMoveChecker {

	private static final HashMap<Class<?>, Boolean> cache = new HashMap<>();

	public static EnumActionResult canMoveClass(Class<?> clazz, World world, BlockPos pos, @Nullable GameProfile profile) {
		IMoveCheck check = ProxyRegistry.getInterface(clazz, IMoveCheck.class, FunkyCapabilities.MOVE_CHECK);
		if (check != null) {
			return check.canMove(world, pos, profile);
		}

		return canMoveClass(clazz) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}

	public static boolean canMoveClass(Class<?> clazz) {
		Boolean b = cache.get(clazz);
		if (b == null) {
			b = _canMoveClass(clazz);
			cache.put(clazz, b);
		}
		return b;
	}

	private static boolean _canMoveClass(Class<?> clazz) {
		try {
			Method method = clazz.getMethod("_Immovable");
			if (Modifier.isStatic(method.getModifiers()) &&
					Modifier.isPublic(method.getModifiers()))
				if (method.getReturnType() == boolean.class) {
					Boolean b = (Boolean) method.invoke(null);
					return b == null || !b;
				}
			return true;
		} catch (NoSuchMethodException | RuntimeException | IllegalAccessException | InvocationTargetException e) {
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return true;
		}
	}


	@Override
	public boolean preventMovement(World world, int x, int y, int z, Block block, int meta, TileEntity tile) {
		return !(canMoveClass(block.getClass()) && (tile == null || canMoveClass(tile.getClass())));
	}
}
