//
// Created by Göksu Güvendiren on 2019-05-14.
//

#include "Scene.hpp"

void Scene::buildBVH() {
    printf(" - Generating BVH...\n\n");
    this->bvh = new BVHAccel(objects, 1, BVHAccel::SplitMethod::NAIVE);
}

Intersection Scene::intersect(const Ray &ray) const
{
    return this->bvh->Intersect(ray);
}

void Scene::sampleLight(Intersection &pos, float &pdf) const
{
    float emit_area_sum = 0;
    for (uint32_t k = 0; k < objects.size(); ++k) {
        if (objects[k]->hasEmit()){
            emit_area_sum += objects[k]->getArea();
        }
    }
    float p = get_random_float() * emit_area_sum;
    emit_area_sum = 0;
    for (uint32_t k = 0; k < objects.size(); ++k) {
        if (objects[k]->hasEmit()){
            emit_area_sum += objects[k]->getArea();
            if (p <= emit_area_sum){
                objects[k]->Sample(pos, pdf);
                break;
            }
        }
    }
}

bool Scene::trace(
        const Ray &ray,
        const std::vector<Object*> &objects,
        float &tNear, uint32_t &index, Object **hitObject)
{
    *hitObject = nullptr;
    for (uint32_t k = 0; k < objects.size(); ++k) {
        float tNearK = kInfinity;
        uint32_t indexK;
        Vector2f uvK;
        if (objects[k]->intersect(ray, tNearK, indexK) && tNearK < tNear) {
            *hitObject = objects[k];
            tNear = tNearK;
            index = indexK;
        }
    }


    return (*hitObject != nullptr);
}

// Implementation of Path Tracing
Vector3f Scene::castRay(const Ray &ray, int depth) const
{

    // TO DO Implement Path Tracing Algorithm here
    Intersection pos =  intersect(ray);
    if(!pos.happened) return Vector3f{};

    if(pos.m->hasEmission()) return pos.m->getEmission();


    Vector3f L_dir{};
    Vector3f L_indir{};

    Vector3f p = pos.coords;
    Material* m = pos.m;
    Vector3f N = pos.normal.normalized();
    Vector3f wo = ray.direction;


    Intersection light_inter;
    float pdf_L = 0.0f;
    sampleLight(light_inter, pdf_L);
    Vector3f x = light_inter.coords;
    Vector3f ws = (x - p).normalized();
    Vector3f NN = light_inter.normal.normalized();
    Vector3f emit = light_inter.emit;


    Ray toLight(p, ws);
    float dis1 = (x-p).norm();
    float dis2 = intersect(toLight).distance;
    if(std::abs(dis1 - dis2) <= 0.001f){
        L_dir = emit * m->eval(wo, ws, N) * dotProduct(ws, N) * dotProduct(-ws, NN) / (dis1 * dis1) / pdf_L;
    }

    float prr = get_random_float();
    if(prr < RussianRoulette){
        auto wi = m->sample(wo, N).normalized();
        Ray r(p, wi);
        Intersection nxt =  intersect(r);
        if(nxt.happened && !nxt.m->hasEmission()){
            L_indir = castRay(r, depth + 1) * m->eval(wo, wi, N) * dotProduct(wi, N) / m->pdf(wo, wi, N) / RussianRoulette;
        }
    }

    return L_dir + L_indir;
}
