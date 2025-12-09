package com.fra.VirtualAcademy.dto;

//Transporta datos entre capas de la aplicación sin exponerlos
public record LoginRequestDto(String email, String password) {
}