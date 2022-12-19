/* eslint-disable*/ 
import axios from 'axios'
export default function ajax(url, data='') {
  url = 'http://localhost:8080/gomoku' + url
  if(data !== '')
    url = url + data;
  return axios.get(url)
  
}
