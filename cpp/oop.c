#include <stdio.h>

struct object {
    int val;
    void (*incval)(struct object *);
    void (*decval)(struct object *);
};

void obj_incval(struct object *obj)
{
    ++obj->val;
}

void obj_decval(struct object *obj)
{
    --obj->val;
}

struct object obj_new(int val)
{
    struct object obj;
    obj.val = val;
    obj.incval = obj_incval;
    obj.decval = obj_decval;
    return obj;
}

int main()
{
    struct object obj = obj_new(5);
    obj.incval(&obj);
    printf("%d\n", obj.val);
    obj.decval(&obj);
    printf("%d\n", obj.val);
}
