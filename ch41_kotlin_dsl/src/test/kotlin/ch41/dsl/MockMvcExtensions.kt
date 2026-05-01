package ch41.dsl

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request
    .MockMvcRequestBuilders.*

fun MockMvc.getJson(url: String): ResultActions =
    perform(
        get(url).accept(MediaType.APPLICATION_JSON)
    )

fun MockMvc.postJson(
    url: String,
    body: String
): ResultActions =
    perform(
        post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
    )

fun MockMvc.deleteFor(url: String): ResultActions =
    perform(delete(url))
