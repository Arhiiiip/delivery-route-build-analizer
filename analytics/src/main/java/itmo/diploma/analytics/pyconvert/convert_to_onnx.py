from transformers import AutoTokenizer, AutoModel
import torch
import onnx

model_name = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModel.from_pretrained(model_name)

sample_text = "This is a sample sentence."
inputs = tokenizer(sample_text, return_tensors="pt", padding=True, truncation=True)

torch.onnx.export(
    model,
    (inputs["input_ids"], inputs["attention_mask"]),
    "paraphrase-multilingual-MiniLM-L12-v2.onnx",
    input_names=["input_ids", "attention_mask"],
    output_names=["output"],
    dynamic_axes={
        "input_ids": {0: "batch_size", 1: "sequence_length"},
        "attention_mask": {0: "batch_size", 1: "sequence_length"},
        "output": {0: "batch_size"}
    },
    opset_version=14
)

print("Модель конвертирована в ONNX!")