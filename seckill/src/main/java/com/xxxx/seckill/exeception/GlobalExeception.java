package com.xxxx.seckill.exeception;

import com.xxxx.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalExeception extends RuntimeException{
    private RespBeanEnum respBeanEnum;
}
