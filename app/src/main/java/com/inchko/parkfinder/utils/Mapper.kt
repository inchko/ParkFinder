package com.inchko.parkfinder.utils

interface Mapper<T,DomainModel>{
fun mapToDomain(t:T):DomainModel
}
