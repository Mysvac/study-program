
I've finished task ...

完成附加项作业

BVH.cpp中的  
`BVHBuildNode* BVHAccel::recursiveBuild(std::vector<Object*> objects)`  
函数。

else块中的条件if(false || objects.size() < 16)

成立时进行的是普通BVH分割，不成立时进行SAH。

可以将false改成true，让整个程序都直接使用BVH。


测试SAH是4秒，BVH是5秒。 但是可能存在随机性误差，提示不明显。

结果图片： binary.ppm

