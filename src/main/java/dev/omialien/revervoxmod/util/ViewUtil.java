package dev.omialien.revervoxmod.util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ViewUtil {
    public static boolean isInSight(LivingEntity pov, LivingEntity entity){
        if(pov.level() != entity.level()){ return false; }
        AABB aabb = entity.getBoundingBox();
        Vec3 p = entity.position();
        double maxY = aabb.maxY;
        double minY = aabb.minY;
        double midY = (maxY + minY) / 2;
        Vec3[] points = {
                new Vec3(p.x, maxY, p.z),
                new Vec3(p.x, minY, p.z),
                new Vec3(p.x, midY, p.z)
        };
        boolean found = false;
        for(Vec3 v : points){
            if(canSee(pov, pov.getEyePosition(), v, pov.level())){
                found = true;
                break;
            }
        }
        if(!found){ return false;}
        Vec3 dir = pov.getLookAngle();
        Vec3 target = pov.position().vectorTo(entity.position());
        target = new Vec3(target.x, 0, target.z).normalize();
        return dir.dot(target) > 0.5;
    }

    private static boolean canSee(LivingEntity pov, Vec3 p1, Vec3 p2, Level level){
        if (p1.distanceTo(p2) > 128.0D) {
            return false;
        } else {
            BlockHitResult res = level.clip(new ClipContext(p1, p2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, pov));
            return res.getType() == HitResult.Type.MISS;
        }
    }
}
