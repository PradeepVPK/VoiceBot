import { Component } from '@angular/core';
import { HttpClient  } from '@angular/common/http';
declare var webkitSpeechRecognition: any;

interface SpeechRecognition {
  onstart: any;
  onresult: any;
  onerror: any;
  start: () => void;
  stop: () => void;
  continuous: boolean;
  interimResults: boolean;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title = 'best-buddy';
  recognition: SpeechRecognition;
  recognizedText: string = '';

  constructor(private http: HttpClient) {
    this.recognition = new webkitSpeechRecognition();
    this.recognition.continuous = true;
    this.recognition.interimResults = true;

    this.recognition.onresult = (event: { resultIndex: any; results: string | any[]; }) => {
      let finalTranscript = '';
      let interimTranscript = '';

      for (let i = event.resultIndex; i < event.results.length; ++i) {
        if (event.results[i].isFinal) {
          finalTranscript += event.results[i][0].transcript + ' ';
        } else {
          interimTranscript += event.results[i][0].transcript;
        }
   
      }

      // Show real-time recognized text
      this.recognizedText = finalTranscript + interimTranscript;
     
    };
  }

  startRecording() {
    this.recognition.start();
  }

  stopRecording() {
    this.recognition.stop();
    console.log(this.recognizedText);
    this.sendTextToBackend(this.recognizedText);
  }
  
  sendTextToBackend(text: string) {
    const apiUrl = 'http://localhost:8080/api/chat'; // Replace this with your actual API endpoint
    const requestBody = { text: text };
    

    this.http.post(apiUrl, requestBody, { responseType: 'arraybuffer' }).subscribe(
      (response:ArrayBuffer) => {
        this.playAudioFromBuffer(response);
      },
      (error) => {
        console.error('Error sending text to backend:', error);
      }
    );
  }
  
  private playAudioFromBuffer(buffer: ArrayBuffer) {
    const audioBlob = new Blob([buffer], { type: 'audio/mp3' });
    const audioUrl = URL.createObjectURL(audioBlob);

    const audioElement = new Audio();
    audioElement.src = audioUrl;
    audioElement.play();
  }
}
