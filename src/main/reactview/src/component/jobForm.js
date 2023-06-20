import { Form, Button, Row, Col } from "react-bootstrap";
import { useRef, useState, useEffect } from "react";
import axios from 'axios';
import { DndContext, closestCenter, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import { arrayMove, SortableContext, sortableKeyboardCoordinates, rectSortingStrategy } from '@dnd-kit/sortable';
import { Draggable, SortablePhoto } from './sortablePhoto';

function JobForm() {
    const selectFile = useRef(null);
    const [previewImages, setPreviewImages] = useState([]);
    const [activeId, setActiveId] = useState(null);
    const sensors = useSensors(
        useSensor(PointerSensor)
    );

    function handleDragStart(event) {
        setActiveId(event.active.id);
    }

    function handleDragEnd(event) {
        const { active, over, delta } = event;

        if (active && over && active.id !== over.id && Math.abs(delta.x) > 10) {
            setPreviewImages((prevImages) => {
                const oldIndex = parseInt(active.id);
                const newIndex = parseInt(over.id);
                return arrayMove(prevImages, oldIndex, newIndex);
            });
        }
    }
    const removePreviewImage = (id) => {
        const numericId = parseInt(id, 10);
        setPreviewImages((prevImages) => prevImages.filter((image, index) => index !== numericId));
    };

    const handleFileButtonClick = () => {
        if (selectFile.current) {
            selectFile.current.click();
        }
    };
    const handleFileChange = (event) => {
        const files = event.target.files;
        if (files && files.length > 0) {
            const imageFiles = Array.from(files).filter(file => file.type.startsWith('image/'));
            const readerPromises = imageFiles.map(file => {
                return new Promise((resolve, reject) => {
                    const reader = new FileReader();
                    reader.onloadend = () => {
                        resolve({ preview: reader.result, file });
                    };
                    reader.onerror = reject;
                    reader.readAsDataURL(file);
                });
            });

            Promise.all(readerPromises)
                .then(results => {
                    setPreviewImages(prevImages => [...prevImages, ...results]);
                })
                .catch(error => {
                    console.error('Error reading files:', error);
                });
        }
    };
    const handleSubmit = null;

    function sample5_execDaumPostcode() {
            new daum.Postcode({
                oncomplete: function(data) {
                    var addr = data.address; // 최종 주소 변수

                    // 주소 정보를 해당 필드에 넣는다.
                    document.getElementById("sample5_address").value = addr;
                    // 주소로 상세 정보를 검색
                    geocoder.addressSearch(data.address, function(results, status) {
                        // 정상적으로 검색이 완료됐으면
                        if (status === daum.maps.services.Status.OK) {

                            var result = results[0]; //첫번째 결과의 값을 활용

                            // 해당 주소에 대한 좌표를 받아서
                            var coords = new daum.maps.LatLng(result.y, result.x);
                            // 지도를 보여준다.
                            mapContainer.style.display = "block";
                            map.relayout();
                            // 지도 중심을 변경한다.
                            map.setCenter(coords);
                            // 마커를 결과값으로 받은 위치로 옮긴다.
                            marker.setPosition(coords)
                        }
                    });
                }
            }).open();
        }
    return (
        <Row className="justify-content-center">
            <div className="photo-div">
                <Form.Group>
                    <Row>
                        <Col md={4}>
                            <Button type="button" onClick={handleFileButtonClick} className="btn btn-secondary photo-btn">
                                <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" width="180" height="180" className="bi bi-camera" viewBox="-3 0 22 22">
                                    <path d="M15 12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1h1.172a3 3 0 0 0 2.12-.879l.83-.828A1 1 0 0 1 6.827 3h2.344a1 1 0 0 1 .707.293l.828.828A3 3 0 0 0 12.828 5H14a1 1 0 0 1 1 1v6zM2 4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2h-1.172a2 2 0 0 1-1.414-.586l-.828-.828A2 2 0 0 0 9.172 2H6.828a2 2 0 0 0-1.414.586l-.828.828A2 2 0 0 1 3.172 4H2z"></path>
                                    <path d="M8 11a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5zm0 1a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7zM3 6.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0z"></path>
                                    <text x="37%" y="90%" textAnchor="middle" alignmentBaseline="middle" fontSize="5px">{previewImages.length} / 10</text>
                                </svg>
                                <input ref={selectFile} type="file" name="fileUpload" accept="image/jpeg, image/jpg , image/png, image/bmp" onChange={handleFileChange} multiple style={{ display: "none" }} />

                            </Button>
                        </Col>
                        <Col>
                            <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd} onDragStart={handleDragStart}>
                                <SortableContext items={previewImages.map((_, index) => `${index}`)} strategy={rectSortingStrategy}>
                                    <div className="d-flex flex-wrap mt-3">
                                        {previewImages.map((image, index) => (
                                            image && <Draggable key={index}>
                                                <SortablePhoto id={index} preview={image.preview} removePreviewImage={removePreviewImage} />
                                            </Draggable>
                                        ))}
                                    </div>
                                </SortableContext>
                            </DndContext>
                        </Col>
                    </Row>
                </Form.Group>
                <br />
            </div>
            <br /><br />
            <Form onSubmit={handleSubmit} encType="multipart/form-data">

                <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                    <Col md={2}>
                        <Form.Label className="me-2">
                            상호명
                        </Form.Label>
                    </Col>
                    <Col md={10}>
                        <Form.Control
                            type="text"
                            name="brand"
                        />
                    </Col>
                </Form.Group>
                <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                    <Col md={2}>
                        <Form.Label className="me-2">
                            위치
                        </Form.Label>
                    </Col>
                    <Col md={8}>
                        <Form.Control
                            type="text"
                            name="jobLocation"
                        />
                    </Col>
                    <Col md={2}>
                        <Button type="button" onclick={sample5_execDaumPostcode()} value="주소 검색"><br>
                    </Col>
                </Form.Group>
                <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                    <Col md={2}>
                        <Form.Label className="me-2" style={{ paddingTop: "4px" }}>
                            타입
                        </Form.Label>
                    </Col>
                    <Col md={2}>
                        <Form.Select name="payType" aria-label="Default select example">
                            <option value="시급">시급</option>
                            <option value="일당">일당</option>
                            <option value="월급">월급</option>
                        </Form.Select>
                    </Col>
                    <Col md={2} style={{ paddingLeft: "10px", textAlign: "center" }}>
                        <Form.Label className="me-2" style={{ paddingTop: "4px", marginRight: "-10px" }}>
                            금액
                        </Form.Label>
                    </Col>
                    <Col md={4}>
                        <Form.Control
                            type="text"
                            name="payTotal"
                            style={{ textAlign: "left" }}
                        />
                    </Col>
                    <Col md={2} style={{ paddingLeft: "10px", textAlign: "left" }}>
                        <Form.Label>
                            원
                        </Form.Label>
                    </Col>
                </Form.Group>
                <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                    <Col md={2}>
                        <Form.Label className="me-2">
                            제목
                        </Form.Label>
                    </Col>
                    <Col md={10}>
                        <Form.Control
                            type="text"
                            name="title"
                        />
                    </Col>
                </Form.Group>
                <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                    <Col md={2}>
                        <Form.Label className="me-2">
                            내용
                        </Form.Label>
                    </Col>
                    <Col md={10}>
                        <Form.Control
                            as="textarea"
                            rows={5}
                            name="content"
                        />
                    </Col>
                </Form.Group>
                <Button as="input" type="submit" value="등록" style={{textAlign="center"}}/>
            </Form>
        </Row>
    )
}

export default JobForm;