package com.example.cris.easytourbrasil.utilitarios;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConversorJson {

    JSONObject jsonObject;
    JSONArray jsonArray;

    public JSONObject getJsonObject(String conteudo) throws JSONException {
        return jsonObject.getJSONObject(conteudo);
    }

    public String[] converterDirecoes(String conteudo){

        try {

            jsonObject = new JSONObject(conteudo);
            jsonArray = jsonObject.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return buscarRotas(jsonArray);
    }

    private String[] buscarRotas(JSONArray googleStepsJSON) {

        int qtdSteps = googleStepsJSON.length();
        String[] polyLines = new String[qtdSteps];

        for(int i = 0; i < qtdSteps; i++)
        {
            try {
                polyLines[i] = buscarRota(googleStepsJSON.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  polyLines;

    }

    private String buscarRota(JSONObject googlePathJson) {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }
}
